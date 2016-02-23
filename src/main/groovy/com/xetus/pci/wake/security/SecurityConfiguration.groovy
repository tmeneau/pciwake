package com.xetus.pci.wake.security

import java.util.regex.Pattern

import javax.inject.Inject
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.access.channel.ChannelProcessingFilter
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler
import org.springframework.security.web.csrf.CsrfFilter
import org.springframework.security.web.csrf.CsrfLogoutHandler
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository
import org.springframework.security.web.util.matcher.RegexRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.filter.OncePerRequestFilter

import com.xetus.pci.wake.config.SiteConfiguration

@EnableWebSecurity
class SecurityConfiguration extends WebSecurityConfigurerAdapter {
  
  @Autowired
  private RESTAuthenticationEntryPoint authenticationEntryPoint
  
  @Autowired
  private RESTAuthenticationFailureHandler authenticationFailureHandler
  
  @Autowired
  private RESTAuthenticationSuccessHandler authenticationSuccessHandler

  
  @Inject
  SiteConfiguration siteConfig
  
  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    
    
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
    
    /*
     * evaluate the authClosure, if one has been configured.
     */
    Closure configurator = siteConfig.authConfig.authClosure
    if (configurator != null) {
      siteConfig.authConfig.authClosure(auth)
    }
  }
  
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    
    final HttpSessionCsrfTokenRepository tokenRepository = 
        new HttpSessionCsrfTokenRepository()
    tokenRepository.setHeaderName("X-CSRF-TOKEN")
    
    http.httpBasic().and()
        .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).and()
        .formLogin().successHandler(authenticationSuccessHandler).and()
        .formLogin().failureHandler(authenticationFailureHandler).and()
        .logout()
          .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
          .addLogoutHandler(new CsrfLogoutHandler(tokenRepository)).and()
        .authorizeRequests()
          .antMatchers("/login", "/get-csrf-token").permitAll()
          .anyRequest().authenticated().and()
          .csrf().csrfTokenRepository(tokenRepository)
            .requireCsrfProtectionMatcher(excludeLoginCsrfMatcher()).and()
          .addFilterBefore(new CorsFilter(), ChannelProcessingFilter.class)
          .addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
  }
  
  private RequestMatcher excludeLoginCsrfMatcher() {
    return new RequestMatcher() {
      private Pattern allowedMethods = Pattern.compile(
        "^(GET|HEAD|TRACE|OPTIONS)\$")
      private RegexRequestMatcher loginMatcher = new RegexRequestMatcher(
        "/login", null)

      @Override
      public boolean matches(HttpServletRequest request) {
          if(allowedMethods.matcher(request.getMethod()).matches() ||
            loginMatcher.matches(request))
              return false

          return true
      }
    }
  }
  
  private Filter csrfHeaderFilter() {
    return new OncePerRequestFilter() {
        @Override
        protected void doFilterInternal(HttpServletRequest request,
                HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class
                    .getName())
            if (csrf != null) {
                Cookie cookie = new Cookie("XSRF-TOKEN", csrf.getToken())
                cookie.setPath("/")
                response.addCookie(cookie)
            }
            filterChain.doFilter(request, response)
        }
    };
}
}
