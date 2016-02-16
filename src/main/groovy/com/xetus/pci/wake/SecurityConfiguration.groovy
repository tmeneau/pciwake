package com.xetus.pci.wake

import javax.inject.Inject

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

import com.xetus.pci.wake.config.SiteConfiguration

@EnableWebSecurity
class SecurityConfiguration {
  
  @Inject
  SiteConfiguration siteConfig

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    
    /*
     * evaluate the authClosure, if one has been configured.
     */
    Closure configurator = siteConfig.authConfig.authClosure
    if (configurator != null) {
      siteConfig.authConfig.authClosure(auth)
    }
    
    
    /*
     * In the event the admin user and admin password have been configured,
     * ensure an in memory authentication source is created with the configured
     * admin credentials
     */
    String user = siteConfig.authConfig.adminUser,
           pass = siteConfig.authConfig.adminPass
    if (user != null) {
      auth.inMemoryAuthentication()
            .withUser(user).password(pass).roles("USER", "ADMIN")
    }
  }
}
