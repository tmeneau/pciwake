package com.xetus.pci.wake.notification.mail

import groovy.transform.CompileStatic

import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.DiscriminatorValue
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.Table

import com.xetus.pci.wake.notification.Notification

/**
 * Configuration for an email notification.
 */
@CompileStatic
@Entity
@Table(name = "mail_notification")
@DiscriminatorValue("mail")
class MailNotification extends Notification {
  
  /**
   * The SMTP configuration that should be used to send the email
   */
  @ManyToOne
  @JoinColumn(name = "smtp_config_id")
  SMTPConfig smtpConfig
  
  /**
   * A listing of email addresses to which emails using this email
   * config should be sent.
   */
  @Column(name = "target_addresses")
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "mail_notification_target_addresses")
  @Lob
  List<String> targetAddresses
  
  /**
   * The subject template to use for generating the email. This
   * template will be passed to a {@link
   * groovy.text.SimpleTemplateEngine}'s {@link
   * groovy.text.SimpleTemplateEngine#createTemplate(String)} method.
   * The specific bindings available to the subject template depends
   * on notification event.
   */
  @Column(name = "subject_template")
  @Lob
  String subjectTemplate
  
  /**
   * The message template to use for generating the email. This
   * template will be passed to a {@link
   * groovy.text.SimpleTemplateEngine}'s {@link
   * groovy.text.SimpleTemplateEngine#createTemplate(String)} method.
   * The specific bindings available to the message template depends
   * on notification event.
   */
  @Column(name = "message_template")
  @Lob
  String messageTemplate
  
  /**
   * The content type ("subtype") that should be sent for the email. Default
   * is "text", another popular option is "html"
   */
  @Column(name = "content_type")
  String contentType = "text"
}
