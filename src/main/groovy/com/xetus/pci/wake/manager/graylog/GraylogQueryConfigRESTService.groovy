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

@CompileStatic
@Path("/logmanager/graylog/config/query")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class GraylogQueryConfigRESTService {

  @Inject
  GraylogQueryConfigRepository queryRepo
  
  @GET
  List<GraylogQueryConfig> getAll() {
    return queryRepo.findAll()
  }
  
  @GET
  @Path("/{id}")
  GraylogQueryConfig getConfig(@PathParam("id") Long id) {
    return queryRepo.findOne(id)
  }
  
  @POST
  @Path("/{id}")
  GraylogQueryConfig update(GraylogQueryConfig queryConfig,
                            @PathParam("id") Long id) {
    GraylogQueryConfig original = queryRepo.findOne(id)
    if (original) {
      queryConfig.setId(id)
      return queryRepo.save(queryConfig)
    }
  }
  
  @POST
  GraylogQueryConfig save(GraylogQueryConfig queryConfig) {
    return queryRepo.save(queryConfig)
  }
}
