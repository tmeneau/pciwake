package com.xetus.pci.wake.security

import java.security.Principal;

import groovy.transform.CompileStatic

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;

@Path("/get-csrf-token")
@Component
@CompileStatic
class CSRFRESTService {

  @GET
  Response get(@Context HttpServletRequest request, Principal user) {
    CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
    return Response.ok(user).header("X-CSRF-TOKEN", token.token).build();
  }
}
