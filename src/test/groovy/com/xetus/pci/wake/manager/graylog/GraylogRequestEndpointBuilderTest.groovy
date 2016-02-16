package com.xetus.pci.wake.manager.graylog

import static org.junit.Assert.assertEquals

import org.joda.time.DateTime
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters

import com.xetus.pci.wake.manager.LogManagerConnectionConfig

@RunWith(Parameterized.class)
class GraylogRequestEndpointBuilderTest {
  
  @Parameter(0)
  public String testName
  
  @Parameter(1)
  public GraylogClientConfig config
  
  @Parameter(2)
  public Date startDate
  
  @Parameter(3)
  public Date endDate
  
  @Parameter(4)
  public URI expected
  
  @Test
  public void test() {
    URI actual = new GraylogURIBuilder()
      .getRequestEndpoint(config, startDate, endDate)
    
    println ("expected: " + expected.toString()
         + "\nactual:   " + actual.toString())
    
    assertEquals(expected, actual)
  }
  
  @Parameters(name = "GraylogRequestURIResolverTest {index}: {0}")
  public static List<Object[]> getParameters() {
    List<Object[]> testCases = []
    
    GraylogClientConfig clientConfig = new GraylogClientConfig(
      queryConfig: new GraylogQueryConfig(queryString: "streamId=STREAM_ID"), 
      connectionConfig: new LogManagerConnectionConfig(
        host: new URI("http://www.test.com")
      )
    )
    
    /*
     * TODO: figure out what timezone considerations must be made!!
     */
    Date startTime = new GregorianCalendar(2016, 1, 4).getTime()
    Date endTime = new GregorianCalendar(2016, 1, 5).getTime()
    testCases << ([
      "Test request generation behaves as expected",
      clientConfig,
      startTime,
      endTime,
      new URI("http://www.test.com/search/universal/absolute?" +
            "query=streamId%3DSTREAM_ID&" +
            "from=" + URLEncoder.encode(new DateTime(startTime.time).toString(), "UTF-8") + 
            "&to=" + URLEncoder.encode(new DateTime(endTime.time).toString(), "UTF-8"))
    ] as Object[])
  }
  
}
