package com.xetus.pci.wake.manager.graylog

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import org.apache.http.conn.HttpClientConnectionManager
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.springframework.stereotype.Component

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.databind.SerializationFeature

import com.xetus.pci.wake.manager.AbstractLogManagerClientPlugin
import com.xetus.pci.wake.manager.LogManagerClientConfig
import com.xetus.pci.wake.manager.LogQueryResult
import com.xetus.pci.wake.manager.fail.TransientLogManagerQueryException

@Slf4j
@Component
@CompileStatic
class GraylogClient 
      extends AbstractLogManagerClientPlugin<GraylogClientConfig> {
  
  static final String GRAYLOG_TYPE_NAME = "graylog"
  private static final ObjectReader JSON_READER
  private static final ObjectWriter JSON_WRITER
  static {
    ObjectMapper om = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .configure(SerializationFeature.INDENT_OUTPUT, true)
    JSON_READER = om.readerFor(GraylogQueryResponse.class)
    JSON_WRITER = om.writerFor(GraylogQueryResponse.class)
  }
  
  GraylogURIBuilder uriResolver = new GraylogURIBuilder()
  
  public GraylogClient() {}
  
  public LogQueryResult getLogs(GraylogClientConfig config, 
                                Date startDate, 
                                Date endDate) {

    URI endpoint = uriResolver.getRequestEndpoint(config, startDate, endDate)
    LogQueryResult result = new LogQueryResult(requestEndpoint: endpoint)
    
    try {
      String response = getResultText(config, endpoint)
      if (response == null) {
        throw new TransientLogManagerQueryException("Failed to retrieve "
          + "response from Graylog instance")
      }
      
      log.debug "response: $response"
      GraylogQueryResponse queryResponse = (GraylogQueryResponse) JSON_READER
          .readValue(response)
      
      if (queryResponse.errorType != null || 
          queryResponse.errorMessage != null) {
        throw new RuntimeException("Received error from Graylog: "
          + JSON_WRITER.writeValueAsString(queryResponse))
      }
      List<GraylogQueryResultMessageWrapper> wrappers = queryResponse.results
      
      result.incidentUrls = []
      wrappers.each { GraylogQueryResultMessageWrapper it -> 
        result.incidentUrls << uriResolver.getMessageReference(config, it)
      }
    
    } catch(e) {
      log.error("Exception raised while attempting to query Graylog "
              + "instance", e)
      throw e
    }
    
    return result;
  }
}
