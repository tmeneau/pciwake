package com.xetus.pci.wake.manager.graylog

import groovy.transform.CompileStatic

import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

import com.xetus.pci.wake.manager.LogManagerConnectionConfigRepository


@CompileStatic
@Path("/logmanager/graylog/config/client")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class GraylogConfigRESTService {

  @Inject
  GraylogClientConfigRepository graylogConfigRepo
  
  @Inject
  GraylogQueryConfigRepository queryConfigRepo
  
  @Inject
  LogManagerConnectionConfigRepository cxnConfigRepo
  @GET
  List<GraylogClientConfig> getConfigs() {
    return graylogConfigRepo.findAll()
  }
  
  @GET
  @Path("/{id}")
  GraylogClientConfig getConfig(@PathParam("id") Long id) {
    return graylogConfigRepo.findOne(id)
  }
  
  @POST
  @Path("/{id}")
  GraylogClientConfig update(GraylogClientConfig clientConfig,
                             @PathParam("id") Long id) {
    GraylogClientConfig original = graylogConfigRepo.findOne(id)
    if (original) {
      clientConfig.setId(id);
      return graylogConfigRepo.save(clientConfig)
    }
    
    return null
  } 
  
  @POST
  GraylogClientConfig createConfig(GraylogClientConfig clientConfig) {
    clientConfig.connectionConfig = cxnConfigRepo.findOne(
      clientConfig.connectionConfig?.id)
    clientConfig.queryConfig = queryConfigRepo.findOne(
      clientConfig.queryConfig?.id)
    
    return graylogConfigRepo.save(clientConfig)
  }
}
