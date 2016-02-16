package com.xetus.pci.wake.config

import groovy.transform.CompileStatic

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder

import com.xetus.pci.wake.SecurityConfiguration
import com.xetus.pci.wake.SecurityWebApplicationInitializer

@CompileStatic
class AuthenticationConfiguration {

  /**
   * A closure that can be used to build the authentication manager
   * configuration that should be used for authenticating requests to the
   * instance. This will be evaluated in the {@link 
   * SecurityConfiguration#configureGlobal(AuthenticationManagerBuilder)}
   * and will be delegated to the {@link AuthenticationManagerBuilder} instance.
   * 
   * This allows the consumer complete flexibility over the authentication 
   * configuration that is used in the application instance.
   */
  Closure authClosure = null
  
  /**
   * The admin user to configure. This is a safety for use in the event
   * the authentication source configured in {@link #authClosure} becomes
   * unreachable at any point in time. Defaults to <code>null</code>.
   * 
   * <p>In the event this is configured to <code>null</code>, no safety
   * admin user will be configured.
   */
  String adminUser = null
  
  /**
   * The admin user pass to configure. Defaults to <code>null</code>.
   */
  String adminPass = null
}
