package com.xetus.pci.wake.manager

import groovy.transform.CompileStatic

import javax.persistence.DiscriminatorColumn
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Inheritance
import javax.persistence.InheritanceType
import javax.persistence.Table

/**
 * The query configuration that should be used to retrieve incidents for a
 * particular {@link com.xetus.pci.wake.review.LogReviewJobConfig}. Specific
 * log manager types may need to define their own subclass to handle any
 * log manager specific requirements
 */
@CompileStatic
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "log_manager_type")
@Table(name = "log_manager_query_config")
class LogManagerQueryConfig {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id
}
