package com.xetus.pci.wake.scheduler

import groovy.util.logging.Slf4j

import javax.inject.Inject

import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

import org.quartz.ObjectAlreadyExistsException
import org.quartz.TriggerKey

import com.xetus.pci.wake.review.LogReviewJobConfig
import com.xetus.pci.wake.review.LogReviewJobConfigRepository


@Slf4j
@Path("/schedule")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SchedulerRESTService {
  
  @Inject
  LogReviewJobConfigRepository configRepo
  
  @Inject
  SchedulerEngine engine
  
  @POST
  @Path("/submit/{id}")
  SchedulerSubmissionResult submit(@PathParam("id") Long id) {
    LogReviewJobConfig config = configRepo.findOne(id)
    if (config == null) {
      return new SchedulerSubmissionResult(
        status: SchedulerSubmissionStatus.INVALID_ID,
        message: SchedulerSubmissionStatus.INVALID_ID.message,
        submittedConfigId: id
      )
    }
    
    try {
      engine.scheduleLogReview(config)
    } catch(ObjectAlreadyExistsException e) {
      return new SchedulerSubmissionResult(
        status: SchedulerSubmissionStatus.ALREADY_SCHEDULED,
        message: SchedulerSubmissionStatus.ALREADY_SCHEDULED.message + ": " 
               + e.message,
        submittedConfigId: id
      )
    } catch(e) {
      LogReviewJobConfig duplicate = configRepo
          .findByJobNameAndJobGroup(config.jobName, config.jobGroup)
      log.error("exception raised scheduling log review", e)
      return new SchedulerSubmissionResult(
        status: SchedulerSubmissionStatus.ERROR,
        message: SchedulerSubmissionStatus.ERROR.message + ": " + e.message,
        submittedConfigId: id,
        duplicatedConfigId: duplicate?.id
      )
    }
    return new SchedulerSubmissionResult(
      status: SchedulerSubmissionStatus.SUCCESS,
      message: SchedulerSubmissionStatus.SUCCESS.message,
      submittedConfigId: id
    )
  }
  
  @POST
  @Path("/unrgesiter/{jobGroup}/{jobName}")
  Map remove(@PathParam("jobGroup") String jobGroup, 
             @PathParam("jobName") String jobName) {
    try {
      engine.schedulerFactory.scheduler
        .unscheduleJob(TriggerKey.triggerKey(jobName, jobGroup))
    } catch(e) {
      log.error("exception raised while attempting to remove job $jobName "
              + "in group $jobGroup", e)
      return [
        "status": "ERROR",
        "message": "Failed to remove job: ${e.message}"
      ]
    }
    
    return [
      "status": "SUCCESS",
      "message": "Successfully unregistered job"
    ]    
  }
  
}
