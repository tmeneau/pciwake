package com.xetus.pci.wake.manager.graylog

import groovy.transform.CompileStatic

import javax.persistence.Column
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity
import javax.persistence.Table

import com.xetus.pci.wake.manager.LogManagerQueryConfig

@CompileStatic
@Entity
@Table(name = "graylog_query_config")
@DiscriminatorValue(GraylogClient.GRAYLOG_TYPE_NAME)
class GraylogQueryConfig extends LogManagerQueryConfig {
  @Column(name = "query_string")
  String queryString
}
