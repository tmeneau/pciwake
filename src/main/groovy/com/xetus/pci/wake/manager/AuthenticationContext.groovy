package com.xetus.pci.wake.manager

import groovy.transform.CompileStatic

import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.xetus.pci.wake.persistence.CryptoConverter

@CompileStatic
@Entity
@Table(name = "log_manager_client_authentication_context")
class AuthenticationContext {
 
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id
   
  @Convert(converter = CryptoConverter.class)
  String username
  
  @JsonIgnore
  @Convert(converter = CryptoConverter.class)
  private String password
  
  @JsonIgnore
  public String getPassword() {
    return this.password
  }
  
  @JsonProperty("password")
  public void setPassword(String password) {
    this.password = password
  }
}
