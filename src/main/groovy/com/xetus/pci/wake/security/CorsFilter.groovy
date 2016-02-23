package com.xetus.pci.wake.security

import groovy.transform.CompileStatic

import javax.inject.Inject;
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

import com.xetus.pci.wake.config.SiteConfiguration;

@Component
@CompileStatic
@Order(Ordered.HIGHEST_PRECEDENCE)
class CorsFilter implements Filter {
  
  @Inject
  SiteConfiguration siteConfig

  void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
    HttpServletResponse response = (HttpServletResponse) res
    
    response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000")
    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE")
    response.setHeader("Access-Control-Expose-Headers", "X-CSRF-TOKEN")
    response.addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, X-CSRF-TOKEN");
    response.setHeader("Access-Control-Max-Age", "3600")
    
    if (((HttpServletRequest) req).getMethod() != 'OPTIONS') {
      chain.doFilter(req, res)
    }
  }

  void init(FilterConfig filterConfig) {}

  void destroy() {}

}