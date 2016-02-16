package com.xetus.pci.wake.manager

import groovy.transform.CompileStatic

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.DiscriminatorColumn
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Inheritance
import javax.persistence.InheritanceType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id as JsonId

/**
 * A configuration container for all the configuration requirements that a
 * particular scheduled automated log review job will need to establish a
 * connection with the log manager instance and to query the log manager 
 * instance for incidents applicable to the automated log review job.
 * 
 * This should be subclassed for any log manager solution that needs to be 
 * queried, updating the parameterized LogManagerConnectionConfig and 
 * LogManagerQueryConfig with any specific implementation type required by the
 * log manager type, and exposing any additional configuration parameters that
 * are required for the log manager instance type.
 */
@CompileStatic
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "log_manager_type")
@Table(name = "log_manager_client_config")
@JsonTypeInfo(use=JsonId.CLASS, property="_class") 
abstract class LogManagerClientConfig<C extends LogManagerConnectionConfig, 
                                Q extends LogManagerQueryConfig> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id
  
  @ManyToOne
  @JoinColumn(name = "Log_manager_connection_config_id")
  C connectionConfig
  
  @ManyToOne
  @JoinColumn(name = "query_config_id")
  Q queryConfig
  
}
