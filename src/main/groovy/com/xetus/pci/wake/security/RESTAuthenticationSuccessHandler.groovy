package com.xetus.pci.wake.security

import groovy.transform.CompileStatic

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.stereotype.Component

@Component
@CompileStatic
class RESTAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response,
                                      Authentication authentication) 
              throws IOException, ServletException {
    clearAuthenticationAttributes(request)
    response.setStatus(HttpServletResponse.SC_OK)
  }
}
