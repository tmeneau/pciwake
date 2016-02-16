package com.xetus.pci.wake.review

import groovy.transform.CompileStatic

import javax.inject.Inject

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

import com.xetus.pci.wake.manager.LogManagerClientConfigRepository


@CompileStatic
@Path("/logreview/config")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class LogReviewJobConfigRESTService {
  
  @Inject
  LogManagerClientConfigRepository logManagerConfigRepo
  
  @Inject
  LogReviewJobConfigRepository configRepo
  
  @POST
  @Consumes("application/json")
  @Produces("application/json")
  LogReviewJobConfig createConfig(LogReviewJobConfig config) {
    config.logManagerClientConfig = logManagerConfigRepo.findOne(
      config.logManagerClientConfig?.id)
    return configRepo.save(config)
  }
  
  @GET
  @Produces("application/json")
  List<LogReviewJobConfig> getConfigs() {
    return configRepo.findAll()
  }
  
  @Path("/{id}")
  @GET
  @Produces("application/json")
  LogReviewJobConfig getConfig(@PathParam("id") Long id) {
    return configRepo.findOne(id)
  }
}
