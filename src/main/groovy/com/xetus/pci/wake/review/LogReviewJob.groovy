package com.xetus.pci.wake.review

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.inject.Inject

import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

import org.springframework.scheduling.quartz.QuartzJobBean

import com.xetus.pci.wake.manager.LogManagerQueryService
import com.xetus.pci.wake.manager.LogQueryResult
import com.xetus.pci.wake.manager.fail.TransientFailureStrategy
import com.xetus.pci.wake.manager.fail.TransientLogManagerQueryException
import com.xetus.pci.wake.notification.Notification
import com.xetus.pci.wake.notification.NotificationService
import com.xetus.pci.wake.review.incident.LogIncident
import com.xetus.pci.wake.review.incident.LogIncidentStatusType

@Slf4j
@CompileStatic
class LogReviewJob extends QuartzJobBean {

  public static final String CONFIG_ID_KEY = "CONFIG_ID_KEY"

  /**
   * TODO: migrate this to a LogReviewJobBindingPolicy or something
   * @param review
   * @return
   */
  public static Map<String, Object> getBinding(ExecutedLogReview review) {
    return [
      "startDate": review.effectiveReviewStartTime,
      "endDate": review.effectiveReviewEndTime,
      "incidents": review.incidents?.collect { it.reference.toString() },
      "jobGroup": review.jobConfig.jobGroup,
      "jobName": review.jobConfig.jobName
    ]
  }

  @Inject
  LogReviewJobConfigRepository jobConfigRepo

  @Inject
  LogReviewJobQueryStartTimeResolutionService startTimeResolutionService

  @Inject
  LogReviewJobTransientFailureHandler failureHandler

  @Inject
  LogManagerQueryService queryService

  @Inject
  NotificationService notificationService

  @Inject
  ExecutedLogReviewRepository reviewRepo

  LogReviewJobConfig getJobConfig(JobExecutionContext context) {
    Long configId = (Long) context
      .getJobDetail()
        .getJobDataMap()
          .get(CONFIG_ID_KEY)

    if (configId == null) {
      throw new JobExecutionException("Job was scheduled without a "
        + "config ID configured in its JobDataMap! Key must be "
        + CONFIG_ID_KEY);
    }

    return jobConfigRepo.findOne(configId)
  }
  
  Date getEndTime() {
    return new Date()
  }

  @Override
  protected void executeInternal(JobExecutionContext context)
      throws JobExecutionException {

    LogReviewJobConfig jobConfig = getJobConfig(context)
    TransientFailureStrategy failStrategy = jobConfig.getFailureStrategy()
    Date startTime = startTimeResolutionService
      .getStartDate(failStrategy, context)
    Date endTime = getEndTime()

    ExecutedLogReview review = reviewRepo.findFirstByJobConfigAndStatus(
      jobConfig,
      LogReviewStatusType.FAILED_AWAITING_RETRY
    )

    if (review == null) {
      review = new ExecutedLogReview(
        effectiveReviewStartTime: startTime,
        effectiveReviewEndTime: endTime,
        jobConfig: jobConfig,
        status: LogReviewStatusType.IN_PROGRESS
      )
      review = (ExecutedLogReview) reviewRepo.save(review)
    }

    LogQueryResult result = null
    try {
      result = queryService.query(
          jobConfig.logManagerClientConfig,
          startTime,
          endTime
      )
    } catch (TransientLogManagerQueryException e) {
      Date nextFireTime = context.getTrigger().getNextFireTime()
      review.setStatus(failureHandler.handleFailure(jobConfig, nextFireTime, e))
      reviewRepo.save(review)
      if (failStrategy != null && failStrategy.notificationStrategy != null) {
        notificationService.notify(
            failStrategy.notificationStrategy.failureNotifications,
            getBinding(review)
        )
      }
      return
    }

    review.incidents = result.incidentUrls?.collect {
      return new LogIncident(
        reference: it,
        status: LogIncidentStatusType.NOT_REVIEWED,
        review: review
      )
    }

    List<Notification> notifications = []
    if (review.incidents != null && !review.incidents?.empty) {
      review.setStatus(LogReviewStatusType.AWAITING_MANUAL_REVIEW)
      notifications = jobConfig.incidentFoundNotifications
    } else {
      review.setStatus(LogReviewStatusType.COMPLETED)
      notifications = jobConfig.noIncidentFoundNotifications
    }

    reviewRepo.save(review)
    jobConfig.failureStrategy?.registerSuccess(context.getFireTime())
    jobConfigRepo.save(jobConfig)

    /*
     * TODO: currently notifying last to let the notification queue service
     * handle notification failures. However, because the notification queue
     * service is currently only in memory, it'd be better to make one of the
     * following two decisions:
     *
     *  1. fail the entire job run if the notification fails (since users won't
     *  be notified of a manual review requirement if incidents are found); or
     *  2. persist the notification queue used by the notification queue service
     *  so that application failure won't lose any notifications
     */
    notificationService.notify(notifications, getBinding(review))
  }

}
