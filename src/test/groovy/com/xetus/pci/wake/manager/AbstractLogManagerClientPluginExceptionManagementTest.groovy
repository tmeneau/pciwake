package com.xetus.pci.wake.manager

import static org.mockito.Mockito.any
import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.spy
import static org.mockito.Mockito.when

import groovy.transform.CompileStatic

import org.apache.http.NoHttpResponseException
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.conn.ConnectTimeoutException
import org.apache.http.impl.client.CloseableHttpClient

import org.junit.Rule
import org.junit.Test

import org.junit.rules.ExpectedException

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters

import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

import com.xetus.pci.wake.manager.fail.TransientLogManagerQueryException

@CompileStatic
@RunWith(Parameterized.class)
class AbstractLogManagerClientPluginExceptionManagementTest {
  
  class TestLogManagerClientPlugin 
        extends AbstractLogManagerClientPlugin<FirstLogManagerClientConfig> {
    public LogQueryResult getLogs(FirstLogManagerClientConfig config,
                                  Date startDate,
                                  Date endDate) { return null }
  }
  
  @Mock
  CloseableHttpClient httpClient
  
  @Spy
  TestLogManagerClientPlugin logManagerClient
  
  @Rule
  public MockitoRule rule = MockitoJUnit.rule()
  
  @Rule
  public ExpectedException exceptionRule = ExpectedException.none()
  
  @Parameter(0)
  public String testName
  
  @Parameter(1)
  public Throwable httpClientException
  
  @Parameter(2)
  public Class<Exception> expectedException 
  
  @Test
  public void test() {
    if (expectedException != null) {
      exceptionRule.expect(expectedException)
    }
    
    when(httpClient.execute((HttpUriRequest) Matchers.<HttpUriRequest> any()))
        .thenThrow(httpClientException)
    doReturn(httpClient)
      .when(logManagerClient)
        .getHttpClient(
            (FirstLogManagerClientConfig) Matchers.<FirstLogManagerClientConfig> any(), 
            any(URI.class)
        )
        
    logManagerClient.getResultText(
        new FirstLogManagerClientConfig(), 
        new URI("http://www.test.com")
    )
  }
  
  @Parameters(name = "AbstractLogManagerclientPluginExceptionManagementTest {index}: {0}")
  public static List<Object[]> getParameters() {
    List<Object[]> testCases = []
    
    testCases << ([
      "NoHttpResponseException triggers Transient exception",
      new NoHttpResponseException("fake"),
      TransientLogManagerQueryException.class
    ] as Object[])
    
    testCases << ([
      "ConnectTimeoutException triggers Transient exception",
      new ConnectTimeoutException(),
      TransientLogManagerQueryException.class
    ] as Object[])
    
    testCases << ([
      "ConnectException triggers Transient exception",
      new ConnectException("fake"),
      TransientLogManagerQueryException.class
    ] as Object[])
    
    testCases << ([
      "SockectException triggers Transient exception",
      new SocketException("fake"),
      TransientLogManagerQueryException.class
    ] as Object[])
    
    testCases << ([
      "IOException does not trigger Transient exception",
      new IOException(),
      IOException.class
    ] as Object[])
    
    testCases << ([
      "ProtocolException does not trigger Transient exception",
      new ProtocolException(),
      ProtocolException.class
    ] as Object[])
    
    return testCases
  }
  
}
