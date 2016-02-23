package com.xetus.pci.wake.manager

import groovy.transform.CompileStatic

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.xml.bind.annotation.XmlElement

@CompileStatic
@Entity
@Table(name = "log_manager_connection_config")
class LogManagerConnectionConfig {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id
  
  String name
  
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "auth_context_id")
  @XmlElement
  AuthenticationContext authContext
  
  URI host
}
