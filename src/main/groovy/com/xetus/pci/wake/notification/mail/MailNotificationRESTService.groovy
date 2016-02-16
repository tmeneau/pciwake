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
@Path("/notification/mail")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class MailNotificationRESTService {

  @Inject
  MailNotificationRepository notificationRepo
  
  @Inject
  SMTPConfigRepository configRepo
  
  @GET
  List<MailNotification> getNotifications() {
    return notificationRepo.findAll()
  }
  
  @GET
  @Path("/{id}")
  MailNotification getNotification(@PathParam("id") Long id) {
    return notificationRepo.findOne(id)
  }
  
  @POST
  MailNotification update(MailNotification notification) {
    if (notification?.smtpConfig?.id == null || 
        configRepo.findOne(notification.smtpConfig.id) == null) {
      throw new IllegalArgumentException("Must persist MailConfig instance "
        + "prior to creating MailNotification instance!")
    }
    return notificationRepo.save(notification)
  }
}
