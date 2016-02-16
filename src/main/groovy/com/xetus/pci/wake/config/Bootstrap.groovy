package com.xetus.pci.wake.config

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.util.ContextInitializer

import com.xetus.pci.wake.notification.NotificationRetryJob
import com.xetus.pci.wake.scheduler.SchedulerEngine

@Slf4j
@Service("bootstrap")
@CompileStatic
class Bootstrap {
  
  @Inject 
  SiteConfiguration siteConfig
  
  @Inject
  SchedulerEngine schedulerEngine
  
  @PostConstruct
  void boot(){
    
    // if applicable, configure logback overrides
    if  (siteConfig.externalConfigDir){
      LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory()
      ContextInitializer ci = new ContextInitializer(lc)
      
      def logConfig = new File(siteConfig.externalConfigDir, "logback.groovy")
      // Now, if a logback.groovy file exists, go read it.
      if (logConfig.exists()){
        ci.configureByResource(logConfig.toURI().toURL())
        log.debug "loaded logback overrides from: $logConfig"
      }
    }
    
    /*
     * start / reconfigure the NotificationRetryJob
     */
    String jobName = NotificationRetryJob.class.name,
           jobGroup = schedulerEngine.SYSTEM_JOB_GROUP
    
    boolean notificationRetryJobExists = schedulerEngine.jobExists(
        jobName, jobGroup)
    
    // if necessary, remove the existing job so it can be reconfigured
    if (notificationRetryJobExists) {
      schedulerEngine.removeJob(jobName, jobGroup)
    }
    
    schedulerEngine.scheduleSimpleJob(
        NotificationRetryJob, 
        jobName, 
        jobGroup, 
        siteConfig.notificationRetryJobFrequency
    )
    log.info "scheduled notification retry job"
  }
  
  @PreDestroy
  void teardown() {
    schedulerEngine.schedulerFactory.scheduler.shutdown(true)
  }
}
