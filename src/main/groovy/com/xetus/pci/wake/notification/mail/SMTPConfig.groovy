package com.xetus.pci.wake.notification.mail

import groovy.transform.CompileStatic

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * Email SMTP configuration
 */
@CompileStatic
@Entity
@Table(name = "mail_config")
class SMTPConfig {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id
  
  /**
   * The SMTP host from which to send the email.
   */
  @Column(name = "smtp_host")
  String smtpHost
  
  /**
   * The port on the SMTP host through which SMTP traffic is
   * routed.
   */
  @Column(name = "smtp_port")
  String smtpPort
  
  /**
   * The email address from which the email should be sent.
   */
  @Column(name = "smtp_from")
  String smtpFrom
  
  /**
   * The username that should be used to authenticate with the SMTP
   * host. If neither this nor {@link #smtpPass} are specified, 
   * Authentication is not used. If smtpUser is not specified but
   * smtpPass is, {@link #smtpUser} will be attempted as the user
   * name.
   */
  @Column(name = "smtp_user")
  String smtpUser
  
  /**
   * The smtpPass for the email address from which the email should
   * be sent. Authentication is not used if this is unspecified.
   */
  @Column(name = "smtp_pass")
  String smtpPass

}