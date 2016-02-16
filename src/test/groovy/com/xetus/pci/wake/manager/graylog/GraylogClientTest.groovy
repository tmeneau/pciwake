package com.xetus.pci.wake.manager.graylog

import groovy.transform.CompileStatic

import org.junit.runners.Parameterized.Parameters

import com.xetus.pci.wake.manager.AbstractParameterizedLogManagerClientTest
import com.xetus.pci.wake.manager.LogManagerClientPlugin
import com.xetus.pci.wake.manager.LogManagerConnectionConfig
import com.xetus.pci.wake.manager.LogQueryResult
import com.xetus.pci.wake.manager.fail.TransientLogManagerQueryException;

@CompileStatic
class GraylogClientTest extends AbstractParameterizedLogManagerClientTest {
  
  static GraylogClientConfig config = new GraylogClientConfig(
    queryConfig: new GraylogQueryConfig(queryString: "streamId=STREAM_ID"),
    webHost: new URI("http://web.graylog.domain.com"),
    connectionConfig: new LogManagerConnectionConfig(
      host: new URI("http://server.graylog.domain.com")
    )
  )
  
  @Override
  public LogManagerClientPlugin getClient() {
    return new GraylogClient()
  }
  
  @Parameters(name = "GraylogClientTest {index}: {0}")
  public static Collection<Object[]> getTestCases() {
    List<Object[]> testCases = []
    
    GraylogURIBuilder uriResolver = new GraylogURIBuilder()
    
    Date startDate = new Date(2016, 02, 03)
    Date endDate = new Date(2016, 02, 04)
    testCases << ([
      "no incidents found",
      config,
      startDate,
      endDate,
      """{"messages": [ ] }""",
      new LogQueryResult().with {
        it.requestEndpoint = uriResolver
          .getRequestEndpoint(config, startDate, endDate)
        it.incidentUrls = []
        return it
      },
      null
    ] as Object[])
    
    startDate = new Date(2016, 02, 03)
    endDate = new Date(2016, 02, 04)
    testCases << ([
      "one incident found",
      config,
      startDate,
      endDate,
      """{
        "messages": [{
          "index": "INDEX",
          "message": {
            "_id": "ID"
          }
        }]
      }""",
      new LogQueryResult().with {
        it.requestEndpoint = uriResolver
          .getRequestEndpoint(config, startDate, endDate)
        it.incidentUrls = [
          new URI(config.webHost.toString() + "/messages/INDEX/ID")
        ]
        return it
      },
      null
    ] as Object[])
    
    startDate = new Date(2016, 02, 03)
    endDate = new Date(2016, 02, 04)
    testCases << ([
      "multiple incidents found",
      config,
      startDate,
      endDate,
      """{
        "messages": [{
            "index": "INDEX_1",
            "message": {
              "_id": "ID_1"
            }
          },{
            "index": "INDEX_2",
            "message": {
              "_id": "ID_2"
            }
          }
        ]
      }""",
      new LogQueryResult().with {
        it.requestEndpoint = uriResolver
          .getRequestEndpoint(config, startDate, endDate)
        it.incidentUrls = [
          new URI(config.webHost.toString() + "/messages/INDEX_1/ID_1"),
          new URI(config.webHost.toString() + "/messages/INDEX_2/ID_2")
        ]
        return it
      },
      null
    ] as Object[])
    
    startDate = new Date(2016, 02, 03)
    endDate = new Date(2016, 02, 04)
    testCases << ([
      "RuntimeException thrown when API failure is returned",
      config,
      startDate,
      endDate,
      """{
        "errorType": "APIError",
        "errorMessage": "ERROR"
      }""",
      null,
      RuntimeException.class
    ] as Object[])
    
    startDate = new Date(2016, 02, 03)
    endDate = new Date(2016, 02, 04)
    testCases << ([
      "TransientLogManagerQueryException thrown on null response",
      config,
      startDate,
      endDate,
      null,
      null,
      TransientLogManagerQueryException.class
    ] as Object[])
    
    return testCases
  }
}
