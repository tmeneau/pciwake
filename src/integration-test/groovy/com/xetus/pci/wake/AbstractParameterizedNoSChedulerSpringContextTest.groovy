package com.xetus.pci.wake

import groovy.transform.CompileStatic

import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import org.springframework.test.context.TestContextManager

@CompileStatic
@RunWith(Parameterized.class)
abstract class AbstractParameterizedNoSChedulerSpringContextTest 
               extends AbstractNoSchedulerSpringContextTest {
  
  
  private TestContextManager testContextManager
  
  @Before
  public void setUpStringContext() throws Exception {
    testContextManager = new TestContextManager(getClass())
    testContextManager.prepareTestInstance(this)
  }
}
