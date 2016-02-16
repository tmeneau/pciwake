package com.xetus.pci.wake.manager.fail

import groovy.transform.CompileStatic

import javax.inject.Inject

import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Consumes
import javax.ws.rs.Produces

import javax.ws.rs.core.MediaType

@Path("/logmanager/failure")
@CompileStatic
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class TransientFailureRESTService {

  @Inject
  TransientFailureJobRecoveryStrategyRepository jobRecoveryStrategyRepo
  
  @Inject
  TransientFailureNotificationStrategyRepository notificationStrategyRepo
  
  @GET
  @Path("/jobrecovery")
  List<TransientFailureJobRecoveryStrategy> getJobRecoveryStrategies() {
    return jobRecoveryStrategyRepo.findAll()
  }
  
  @GET
  @Path("/jobrecovery/{id}")
  TransientFailureJobRecoveryStrategy getJobRecoveryStrategy(@PathParam("id") Long id) {
    return jobRecoveryStrategyRepo.findOne(id)
  }
  
  @POST
  @Path("/jobrecovery")
  TransientFailureJobRecoveryStrategy updateJobRecoveryStrategy(TransientFailureJobRecoveryStrategy jobRecoveryStrategy) {
    return jobRecoveryStrategyRepo.save(jobRecoveryStrategy)
  }
  
  @GET
  @Path("/notification")
  List<TransientFailureNotificationStrategy> getNotificationStrategies() {
    return notificationStrategyRepo.findAll()
  }
  
  @GET
  @Path("/notification/{id}")
  TransientFailureNotificationStrategy getNotificationStrategy(@PathParam("id") Long id) {
    return notificationStrategyRepo.findOne(id)
  }
  
  @POST
  @Path("/notification")
  TransientFailureNotificationStrategy updateNotificationStrategy(TransientFailureNotificationStrategy notificationStrategy) {
    return notificationStrategyRepo.save(notificationStrategy)
  }
  
  
}
