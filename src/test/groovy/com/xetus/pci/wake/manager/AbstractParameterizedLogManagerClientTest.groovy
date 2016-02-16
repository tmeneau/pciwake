package com.xetus.pci.wake.manager

import static org.junit.Assert.assertEquals

import static org.mockito.Mockito.any
import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.spy

import groovy.json.JsonOutput
import groovy.transform.CompileStatic

import org.junit.Rule;
import org.junit.Test
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.mockito.Matchers

/**
 * Simple mocking unit tests to ensure {@link LogQueryResult} parsing of 
 * anticipated log manager query results is occurring as expected
 */
@RunWith(Parameterized.class)
@CompileStatic
abstract class AbstractParameterizedLogManagerClientTest<T extends LogManagerClientConfig> {
  
  @Parameter(0)
  public String testName
  
  @Parameter(1)
  public T config
  
  @Parameter(2)
  public Date startDate
  
  @Parameter(3)
  public Date endDate
  
  @Parameter(4)
  public String jsonResponse
  
  @Parameter(5)
  public LogQueryResult expected
  
  @Parameter(6)
  public Class<Throwable> exceptionClazz
  
  @Rule
  public ExpectedException exceptionRule = ExpectedException.none()
  
  abstract LogManagerClientPlugin<T> getClient()
  
  @Test
  public void test() {
    LogManagerClientPlugin<T> logManagerClient = spy(getClient())
    doReturn(jsonResponse)
      .when(logManagerClient)
        .getResultText(Matchers.<T> any(), any(URI.class))
    
    if (exceptionClazz != null) {
      exceptionRule.expect(exceptionClazz)
    }
    
    LogQueryResult actual = logManagerClient.getLogs(config, startDate, endDate)
    
    println ("expected: " + JsonOutput.prettyPrint(JsonOutput.toJson(expected))
           + "\nactual: " + JsonOutput.prettyPrint(JsonOutput.toJson(actual)))
    
    assertEquals(expected, actual)
  }
  
}
