package com.xetus.pci.wake.manager

import groovy.transform.CompileStatic

import org.springframework.data.jpa.repository.JpaRepository

@CompileStatic
interface LogManagerQueryRepository 
          extends JpaRepository<LogManagerQueryConfig, Long> {}
