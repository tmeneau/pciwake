package com.xetus.pci.wake.review

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.inject.Inject

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType

import com.xetus.pci.wake.manager.fail.TransientFailureJobRecoveryStrategyRepository
import com.xetus.pci.wake.manager.fail.TransientFailureNotificationStrategyRepository
import com.xetus.pci.wake.notification.NotificationService
import com.xetus.pci.wake.review.incident.LogIncident
import com.xetus.pci.wake.review.incident.LogIncidentRepository

@Slf4j
@Path("/logreview")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CompileStatic
class LogReviewRESTService {

  @Inject
  ExecutedLogReviewRepository reviewRepo
  
  @Inject
  LogIncidentRepository incidentRepo
  
  @Inject
  NotificationService notificationService
  
  @Inject
  TransientFailureJobRecoveryStrategyRepository jobRecoveryRepo
  
  @Inject
  TransientFailureNotificationStrategyRepository notificationStrategyRepository
  
  @Inject
  LogReviewJobConfigRepository reviewConfigRepo
  
  @GET
  List<ExecutedLogReview> getReviews(@QueryParam("reviewConfigId") Long jobConfigId) {
    if (jobConfigId) {
      return reviewRepo.findByJobConfig(new LogReviewJobConfig(id: jobConfigId))
    }
    return reviewRepo.findAll()
  }
  
  @GET
  @Path("/status/{status}")
  List<ExecutedLogReview> getUnresolvedReviews(@QueryParam("reviewConfigId") Long jobConfigId,
                                               @PathParam("status") LogReviewStatusType status) {
    if (jobConfigId) {
      reviewRepo.findByJobConfigAndStatus(
        new LogReviewJobConfig(id: jobConfigId), status)
    }
    return reviewRepo.findByStatus(status)
  }
  
  @GET
  @Path("/{id}")
  ExecutedLogReview getReview(@PathParam("id") Long id) {
    return reviewRepo.findOne(id)
  }

  @POST
  @Path("/{id}/")
  ExecutedLogReview updateReviewMessage(@PathParam("id") Long id,
                                        ExecutedLogReview review) {
    ExecutedLogReview original = reviewRepo.findOne(id)
    if (review == null) {
      throw new IllegalStateException("No review found for supplied ID")
    }
    original.reviewerMessage = review.reviewerMessage
    return reviewRepo.save(original)
  }
  
  @GET
  @Path("/{reviewId}/{incidentId}")
  LogIncident getIncident(@PathParam("reviewId") Long reviewId,
                          @PathParam("incidentId") Long incidentId) {
    LogIncident incident = incidentRepo.findOne(incidentId)
    if (incident.review?.id != reviewId) {
      throw new IllegalStateException("Attempted to request incident for wrong "
        + "review ID")
    }
    return incident
  }
  
  @POST
  @Path("/{reviewId}/{incidentId}")
  ExecutedLogReview updateIncidentStatus(@PathParam("reviewId") Long reviewId,
                                         @PathParam("incidentId") Long incidentId,
                                         LogIncident incident) {
    ExecutedLogReview review = reviewRepo.findOne(reviewId)
    if (review == null) {
      throw new IllegalArgumentException("No review found for id: $reviewId")
    }
    
    LogReviewStatusType originalStatus = review.status
    review.updateIncident(
      incidentId, 
      "TODO", 
      incident.status, 
      incident.statusMessage
    )
    reviewRepo.save(review)
    
    if (review.status == LogReviewStatusType.COMPLETED && 
        originalStatus != review.status) {
      notificationService.notify(
        review.jobConfig.reviewResolvedNotifications, 
        LogReviewJob.getBinding(review)
      )
    }
    
    return review
  }
}
