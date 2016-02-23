package com.xetus.pci.wake.config

import groovy.transform.CompileStatic
import groovy.transform.ToString

import org.quartz.CronExpression

import org.springframework.stereotype.Component

@ToString(includeNames = true)
@Component
@CompileStatic
class SiteConfiguration {
  
  /**
   * The database connection configuration that should be used by Spring to
   * connect to the database
   */
  DatabaseConfiguration dbConfig = new DatabaseConfiguration()
  
  /**
   * The path to the external configuration directory where configuration
   * overrides are stored. Defaults to null, which does not use any 
   * configuration overrides
   */
  File externalConfigDir = null
  
  /**
   * The frequency with which the {@link NotificationRetryJob} should be 
   * executed. Defaults to every five minutes.
   */
  CronExpression notificationRetryJobFrequency = new CronExpression("0 0/5 * * * ?")
  
  /**
   * The authentication configuration that should be used for configuring
   * authentication for the application.
   */
  AuthenticationConfiguration authConfig = new AuthenticationConfiguration()
  
  String allowedOrigins = "http://localhost:3000";
  
  DatabaseConfiguration db(@DelegatesTo(DatabaseConfiguration) Closure cl){
    cl.delegate = dbConfig
    cl.resolveStrategy =  Closure.DELEGATE_FIRST
    cl()
    dbConfig
  }
  
  AuthenticationConfiguration auth(@DelegatesTo(AuthenticationConfiguration) Closure cl) {
    cl.delegate = authConfig
    cl.resolveStrategy = Closure.DELEGATE_FIRST
    cl()
    authConfig
  }
  

}
