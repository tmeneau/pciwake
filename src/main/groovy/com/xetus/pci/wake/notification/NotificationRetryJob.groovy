package com.xetus.pci.wake.notification

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.inject.Inject

import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Service

/**
 * A very simple notification retry job that can be scheduled to retry
 * jobs queued with the {@link NotificationService}.
 */
@Slf4j
@Service
@CompileStatic
class NotificationRetryJob extends QuartzJobBean {

  static final String JOB_NAME = NotificationRetryJob.class.name
  
  @Inject
  NotificationService notificationService
  
  @Override
  protected void executeInternal(JobExecutionContext context)
      throws JobExecutionException {
    log.debug "starting notification retry job..."
    
    def shouldLog = notificationService.queuedNotifications.size() > 0
    if (shouldLog) {
      log.info "retrying ${notificationService.queuedNotifications.size()} " +
            "queued notifications..."
    } 
    notificationService.queuedNotifications.each {
      notificationService.retryQueued(it)
    }
    
    if (shouldLog) {
      log.info "finished retrying notifications with an outsatnding " +
               "${notificationService.queuedNotifications.size()} still queued"
    }
    
    log.debug "completed notification retry job"
  }
}
