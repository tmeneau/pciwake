package com.xetus.pci.wake.manager.graylog

import groovy.transform.CompileStatic

import org.springframework.data.jpa.repository.JpaRepository

@CompileStatic
interface GraylogQueryConfigRepository
          extends JpaRepository<GraylogQueryConfig, Long> {}
