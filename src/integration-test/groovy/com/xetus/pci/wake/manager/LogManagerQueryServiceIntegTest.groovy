package com.xetus.pci.wake.manager

import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail

import groovy.transform.CompileStatic

import javax.inject.Inject

import org.junit.Test

import com.xetus.pci.wake.AbstractNoSchedulerSpringContextTest

@CompileStatic
class LogManagerQueryServiceIntegTest extends AbstractNoSchedulerSpringContextTest {
  
  @Inject
  LogManagerQueryService queryService
  
  @Test
  void testQueryServiceRetrievesCorrectPlugin() {
    try {
      LogQueryResult firstResult = queryService
        .query(new FirstLogManagerClientConfig(), new Date(), new Date())
      assertEquals("Expected first log manager query result", 
        FirstTestLogManagerClientPlugin.LOG_RESULT, firstResult)
    } catch(IllegalArgumentException e) {
      fail(e.message)
    }
    
    try {
      LogQueryResult secondResult = queryService
        .query(new SecondLogManagerClientConfig(), new Date(), new Date())
      assertEquals("Expected second log manager query result", 
        SecondTestLogManagerClientPlugin.LOG_RESULT, secondResult)
    } catch(IllegalArgumentException e) {
      fail(e.message)
    }
  }
}
