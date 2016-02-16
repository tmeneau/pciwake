package com.xetus.pci.wake.review

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.inject.Inject

import org.springframework.stereotype.Service

import com.xetus.pci.wake.manager.fail.TransientFailureStrategy
import com.xetus.pci.wake.scheduler.SchedulerEngine

/**
 * A simple service implementing the logic for handling retrying log reviews 
 * that experienced what appear to be transient failures during their run. 
 * 
 * <p>This handler is only responsible for re-scheduling the job if applicable 
 * and updating the registered {@link  LogReviewJobConfig#getFailureStrategy()} 
 * to reflect whichever action is taken in the handler; it is up to the caller
 * to persist the computed {@link LogReviewStatusType} to the {@link 
 * ExecutedLogReview} instance and to perform any applicable notifications. 
 */
@Slf4j
@Service
@CompileStatic
class LogReviewJobTransientFailureHandler {
  
  @Inject
  SchedulerEngine schedulerEngine
  
  @Inject
  LogReviewJobConfigRepository jobConfigRepo

  /**
   * Returns the next retry time that should be scheduled based on the 
   * supplied {@link TransientFailureStrategy}
   * 
   * @param failStrategy
   * 
   * @return the next retry time to schedule
   */
  Date getNextRetryTime(TransientFailureStrategy failStrategy) {
    return failStrategy.jobRecoveryStrategy.retryInterval
      .getNextValidTimeAfter(new Date())
  }
  
  /**
   * Handles a log review job failure for the supplied {@link 
   * LogReviewJobConfig} and configured {@link 
   * LogReviewJobConfig#getTransientFailurestrategy()} instance, including:
   * 
   * <ol>
   *  <li>determining whether the current failed log review should be retried 
   *  or permanently failed. The job is permanently failed if one of the 
   *  following is true:
   *  <ul>
   *    <li>the {@link TransientFailureStrategy#retryAttemptsExpired()} returns 
   *    true
   *    <li>the supplied job's normal next fire time will happen before the
   *    next retry time returned by the {@link 
   *    #getNextRetryTime(TransientFailureStrategy)} method
   *  </ul>
   *  <li> if applicable, scheduling a retry run of the log review with the 
   *  trigger time returned by the {@link 
   *  #getNextRetryTime(TransientFailureStrategy)} method.
   * </ul>
   *   
   * @param config the {@link LogReviewJobConfig} instance for the failed 
   * log review execution
   * @param nextFireTime the next normal execution time for the failed log
   * review execution
   * @param e the exception that triggered this failure handler (for logging
   * purposes)
   * 
   * @return the {@link LogReviewStatusType} determined applicable given
   * the supplied <code>config</code> and <code>nextFireTime</code> values.
   */
  public LogReviewStatusType handleFailure(LogReviewJobConfig config,
                                           Date nextFireTime,
                                           Exception e) {
    TransientFailureStrategy failStrategy = config?.getFailureStrategy()
    if (failStrategy != null) {
      Date nextRetry = getNextRetryTime(failStrategy) 
      Integer attemptedRetries = failStrategy.getRetryAttempts()
      
      /*
       * Check if we should fail the current review and let the next retry
       * be on the next normally scheduled review run
       */
      if (failStrategy.retryAttemptsExpired()) {
        failStrategy.registerSurrender();
        jobConfigRepo.save(config)
        log.error("Encountered transient exception and expired retry attempts "
          + "with ${attemptedRetries} retries of "
          + "${failStrategy.jobRecoveryStrategy?.retryLimit}. Will "
          + "not retry until next normal fire time.", e)
        return LogReviewStatusType.FAILED
      } else
      if (nextFireTime != null && nextFireTime <= nextRetry) {
        failStrategy.registerSurrender();
        jobConfigRepo.save(config)
        log.error("Encountered transient exception and next fire time "
          + "$nextRetry would occur after next normal fire time $nextFireTime; "
          + "failing this log review. Will not retry until next normal fire "
          + "next normal fire time.", e)
        return LogReviewStatusType.FAILED
      }
      
      failStrategy.registerFailedRetry();
      jobConfigRepo.save(config)
      schedulerEngine.scheduleLogReviewRetry(nextRetry, config)
      log.error("Encountered transient exception and have not yet reached "
        + "retry limit of "
        + "${failStrategy.jobRecoveryStrategy.retryLimit} with retry "
        + "count of $attemptedRetries", e)
      return LogReviewStatusType.FAILED_AWAITING_RETRY
    }
    
    /*
     * No fail strategy is configured, so fail the current review and let the
     * next normal review run retry
     */
    return LogReviewStatusType.FAILED
  }
}
