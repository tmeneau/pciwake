package com.xetus.pci.wake

import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests

import groovy.transform.CompileStatic


@CompileStatic
@ContextConfiguration(locations = [
  "/noSchedulerTestApplicationContext.groovy"
])
abstract class AbstractNoSchedulerSpringContextTest 
               extends AbstractJUnit4SpringContextTests {}
