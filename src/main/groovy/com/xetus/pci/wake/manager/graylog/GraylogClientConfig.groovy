package com.xetus.pci.wake.manager.graylog

import groovy.transform.CompileStatic

import javax.persistence.Column
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity
import javax.persistence.Table

import com.xetus.pci.wake.manager.LogManagerClientConfig
import com.xetus.pci.wake.manager.LogManagerConnectionConfig

@CompileStatic
@Entity
@Table(name = "graylog_client_config")
@DiscriminatorValue(GraylogClient.GRAYLOG_TYPE_NAME)
class GraylogClientConfig extends LogManagerClientConfig<LogManagerConnectionConfig, 
                                             GraylogQueryConfig> {
  @Column(name = "web_host")
  URI webHost
}
