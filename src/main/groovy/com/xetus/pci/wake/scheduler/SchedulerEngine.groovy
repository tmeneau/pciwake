package com.xetus.pci.wake.scheduler

import groovy.transform.CompileStatic

import javax.annotation.PostConstruct
import javax.inject.Inject

import org.quartz.CronExpression
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.JobDetail
import org.quartz.JobKey
import org.quartz.Trigger
import org.quartz.TriggerBuilder

import org.quartz.impl.triggers.SimpleTriggerImpl

import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.scheduling.quartz.SpringBeanJobFactory

import org.springframework.stereotype.Service

import com.xetus.pci.wake.review.LogReviewJob
import com.xetus.pci.wake.review.LogReviewJobConfig

@Service
@CompileStatic
class SchedulerEngine {
  
  static final String SYSTEM_JOB_GROUP = "__pciwake__"

  @Inject
  SchedulerFactoryBean schedulerFactory
  
  @Inject
  SpringBeanJobFactory springJobFactory
  
  @PostConstruct
  public void setup() {
    schedulerFactory.setJobFactory(springJobFactory)
  }
  
  /**
   * Schedules a log review using the supplied {@link LogReviewJobConfig} 
   * instance.
   * 
   * @param config
   * 
   * @return
   */
  Date scheduleLogReview(LogReviewJobConfig config) {
    JobDetail job = JobBuilder.newJob(LogReviewJob.class)
        .withIdentity(config.jobName, config.jobGroup)
          .build()
    job.jobDataMap.put(LogReviewJob.CONFIG_ID_KEY, config.id)
    
    Trigger trigger = TriggerBuilder.newTrigger()
        .withSchedule(CronScheduleBuilder.cronSchedule(config.frequency))
          .build()
          
    return schedulerFactory.getScheduler().scheduleJob(job, trigger)
  }
  
  /**
   * Convenience method to schedule a one-off log review retry at the supplied 
   * <code>nextRetry</code> date using the supplied {@link LogReviewJobConfig} 
   * instance.
   * 
   * @param nextRetry the date at which to retry
   * @param config
   * 
   * @return
   */
  Date scheduleLogReviewRetry(Date nextRetry, LogReviewJobConfig config) {
    String name = "${config.jobName}-retry"
    String group = config.jobGroup
    
    JobDetail job = JobBuilder.newJob(LogReviewJob.class)
        .withIdentity(name, group)
          .build()
    job.jobDataMap.put(LogReviewJob.CONFIG_ID_KEY, config.id)
    
    Trigger trigger = new SimpleTriggerImpl(name, group,nextRetry)
    
    return (jobExists(name, group)) ?
      schedulerFactory.getScheduler().rescheduleJob(trigger.getKey(), trigger) : 
      schedulerFactory.getScheduler().scheduleJob(job, trigger)
  }
  
  /**
   * A convenience method to schedule a simple job using the supplied 
   * parameters.
   * 
   * @param jobClazz
   * @param jobName
   * @param jobGroup
   * @param cron
   * 
   * @return
   */
  Date scheduleSimpleJob(Class<? extends QuartzJobBean> jobClazz, 
                         String jobName, 
                         String jobGroup, 
                         CronExpression cron) {
    JobDetail job = JobBuilder.newJob(jobClazz)
        .withIdentity(jobName, jobGroup)
          .build()
    Trigger trigger = TriggerBuilder.newTrigger()
        .withSchedule(CronScheduleBuilder.cronSchedule(cron))
          .build()
    
    return schedulerFactory.getScheduler().scheduleJob(job, trigger)
  }

  /**
   * Convenience method for determining whether a job with the specified name
   * and group already exists
   * 
   * @param name
   * @param group
   * 
   * @return
   */
  boolean jobExists(String name, String group) {
    return schedulerFactory.getScheduler().checkExists(new JobKey(name, group))
  }
  
  /**
   * Convenience method for removing a job with the specified name and group 
   * 
   * @param name
   * @param group
   * 
   * @return
   */
  boolean removeJob(String name, String group) {
    return schedulerFactory.getScheduler().deleteJob(new JobKey(name, group))
  }
}
