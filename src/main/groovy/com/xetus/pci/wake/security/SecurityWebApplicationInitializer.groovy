package com.xetus.pci.wake.security

import groovy.transform.CompileStatic

import org.springframework.security.access.SecurityConfig
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer

@CompileStatic
class SecurityWebApplicationInitializer 
      extends AbstractSecurityWebApplicationInitializer {
  
    public SecurityWebApplicationInitializer() {
      super(SecurityConfig.class);
  } 
  
}
