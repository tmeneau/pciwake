package com.xetus.pci.wake.notification.mail

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

import org.springframework.stereotype.Component

import com.sun.mail.util.MailConnectException
import com.xetus.pci.wake.notification.AbstractNotificationProcessorPlugin
import com.xetus.pci.wake.notification.Notification
import com.xetus.pci.wake.notification.TransientNotificationException

@Slf4j
@CompileStatic
@Component
class MailNotificationProcessor 
      extends AbstractNotificationProcessorPlugin<MailNotification> {
  
  Session getSession(SMTPConfig config) {
    Properties props = new Properties()
    props.setProperty("mail.transport.protocol","smtp");
    props.setProperty("mail.smtp.host", config.smtpHost)
    props.setProperty("mail.smtp.port", config.smtpPort)
    props.setProperty("mail.smtp.from", config.smtpFrom)
    
    if (config.smtpPass == null) {
      return Session.getDefaultInstance(props)
    }
    
    props.setProperty("mail.smtp.auth", "true")
    props.put("mail.smtp.starttls.enable", "true");
    return Session.getInstance(
        props,
        new Authenticator() {
          PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(
              config.smtpUser ?: config.smtpFrom, config.smtpPass);
          }
        }
    )
  }
  
  @Override
  public void process(MailNotification notification, 
                      Map<String, Object> dataBinding) {
    Session session = getSession(notification.smtpConfig)
    
    log.debug "Generating email content using the following data: $dataBinding"
    String subject = resolveTemplate(notification.subjectTemplate, dataBinding)
    String content = resolveTemplate(notification.messageTemplate, dataBinding)
      
    MimeMessage message = new MimeMessage(session)
    message.setRecipients(
        MimeMessage.RecipientType.TO, 
        new InternetAddress(notification.targetAddresses.join(","))
    )
    message.setFrom(new InternetAddress(notification.smtpConfig.smtpFrom))
    message.setSubject(subject)
    message.setText(content, "UTF-8", notification.contentType ?: "text")
    
    log.trace """Attempting to send email:
=====
to: ${notification.targetAddresses.join(",")}
subject: $subject
content: $content
=====
"""
    try {
      Transport.send(message)
    } catch(MailConnectException e) {
      throw new TransientNotificationException(e)
    } 
  }

}
