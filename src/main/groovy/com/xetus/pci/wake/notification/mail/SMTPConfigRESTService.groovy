package com.xetus.pci.wake.notification.mail

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
@Path("/notification/mail/smtpconfig")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SMTPConfigRESTService {
  
  @Inject
  SMTPConfigRepository mailConfigRepo
  
  @GET
  public List<SMTPConfig> getConfigs() {
    return mailConfigRepo.findAll()
  }
  
  @GET
  @Path("/{id}")
  SMTPConfig getConfig(@PathParam("id") Long id) {
    return mailConfigRepo.findOne(id)
  }
  
  @POST
  SMTPConfig update(SMTPConfig config) {
    return mailConfigRepo.save(config)
  }
  
}
