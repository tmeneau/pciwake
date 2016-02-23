package com.xetus.pci.wake.manager

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Slf4j
@CompileStatic
@Path("/logmanager/config/connection")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class LogManagerConnectionConfigRESTService {

  @Inject
  LogManagerConnectionConfigRepository configRepo
  
  @GET
  List<LogManagerConnectionConfig> getAll() {
    return configRepo.findAll()
  }
  
  @GET
  @Path("/{id}")
  LogManagerConnectionConfig getConfig(@PathParam("id") Long id) {
    return configRepo.findOne(id)
  }
  
  @POST
  @Path("/{id}")
  LogManagerConnectionConfig update(LogManagerConnectionConfig config,
                                    @PathParam("id") Long id) {
    LogManagerConnectionConfig original = configRepo.findOne(id)
    if (original) {
      config.setId(id)
      return configRepo.save(config)
    }
    return null
  }
  
  @DELETE
  @Path("/{id}")
  Map delete(@PathParam("id") Long id) {
    boolean result = configRepo.delete(id)
    return [
      "success": result
    ]
  }
  
  @POST
  LogManagerConnectionConfig createConfig(LogManagerConnectionConfig config) {
    return configRepo.save(config)
  }
  
}
