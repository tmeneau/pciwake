package com.xetus.pci.wake.manager

import groovy.transform.CompileStatic

import org.springframework.data.jpa.repository.JpaRepository

@CompileStatic
interface LogManagerConnectionConfigRepository 
          extends JpaRepository<LogManagerConnectionConfig, Long> {}
