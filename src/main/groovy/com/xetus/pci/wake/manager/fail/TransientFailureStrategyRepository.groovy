package com.xetus.pci.wake.manager.fail

import groovy.transform.CompileStatic

import org.springframework.data.jpa.repository.JpaRepository

@CompileStatic
interface TransientFailureStrategyRepository 
          extends JpaRepository<TransientFailureStrategy, Long> {}
