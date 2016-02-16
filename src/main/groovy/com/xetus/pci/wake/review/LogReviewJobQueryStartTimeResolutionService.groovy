package com.xetus.pci.wake.review

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import org.joda.time.DateTime
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Service

import com.xetus.pci.wake.manager.fail.TransientFailureStrategy

/**
 * A simple service for determining the start time for which a log review
 * job should query 
 */
@Slf4j
@Service
@CompileStatic
class LogReviewJobQueryStartTimeResolutionService {

  /**
   * Determines the start time, which is evaluated using the earlier of:
   * 
   * <ul>
   *  <li>the previous scheduler fire time for the job, or the difference 
   *  between the next fire time and the current fire time if this is the
   *  first time the job is being run; and</li>
   *  <li>the last successful run time registered with the supplied
   *  {@link TransientFailureStrategy#getLastSuccessfulRun()}.
   * </ul>
   * 
   * @param failStrategy the fail strategy from which to extract the last
   * successful run date
   * @param context the context object from which to extract the previous or
   * current fire time, as applicable. 
   * 
   * @return the start date for which to query the log manager
   */
  Date getStartDate(TransientFailureStrategy failStrategy, 
                    JobExecutionContext context) {
    
    Date nextFireTime = context.getTrigger().getNextFireTime()
    Date startTime = context.getPreviousFireTime()
    /*
     * Determine start time for the log manager query range. While this should
     * be the previous fire time (which bakes in recovery support in the event
     * of application-wide down time), if this is the first time the job is run 
     * then the start time is calculated as the current time, minus the duration 
     * of the next log review range (which is the difference between the next 
     * fire time and the current time).
     */
    if (startTime == null) {
      DateTime difference = new DateTime(nextFireTime)
        .minus(context.getFireTime().getTime())
      
      startTime = new DateTime(context.getFireTime()).minus(
        difference.getMillis()).toDate()
      log.debug "start time was null, using the difference between the " +
                "current fire time (${context.getFireTime().getTime()}) " +
                "and the next fire time ($nextFireTime}"
    }
    
    Date lastSuccess = failStrategy?.lastSuccessfulRun
    log.debug "returning the earlier of start time ($startTime) and the " +
              "last success time ($lastSuccess)"
    /*
     * If the failure strategy has a last successful run configured, then
     * use that in favor of the computed start time since that indicates
     * a date range that remains un-reviewed
     */
    return  lastSuccess != null && lastSuccess < startTime ? 
      lastSuccess : startTime
  }
  
  
}
