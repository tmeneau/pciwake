package com.xetus.pci.wake.manager.graylog

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import java.text.DateFormat
import java.text.SimpleDateFormat
import org.joda.time.DateTime

import org.apache.http.client.utils.URIBuilder

@Slf4j
@CompileStatic
class GraylogURIBuilder {
  
  public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  
  public static String formatDate(Date date) {
    /*
     * TODO: remove dependency on JodaTime
     */
    return new DateTime(date).toString()
  }
  
  URI getRequestEndpoint(GraylogClientConfig config, Date start, Date end) {
    if (start == null) {
      throw new IllegalArgumentException("Start date must not be null")
    }
    
    if (end == null) {
      throw new IllegalArgumentException("End date must not be null")
    }
    
    if (config.connectionConfig == null) {
      throw new IllegalArgumentException("Connection config must not be null")
    }
    
    URI endpoint = new URIBuilder(config.connectionConfig.host)
      .setPath("/search/universal/absolute")
      .addParameter("query", config.queryConfig?.queryString ?: "*")
      .addParameter("from", formatDate(start))
      .addParameter("to", formatDate(end))
      .build()
      
    log.debug("Generated endpoint for $config with $start and $end: $endpoint")
    return endpoint
  }
  
  URI getMessageReference(GraylogClientConfig config, GraylogQueryResultMessageWrapper wrapper) {
    String index = wrapper?.index
    
    GraylogQueryResultMessage message = wrapper?.message
    String messageId = message?._id
    
    if (index == null || messageId == null) {
      log.error("Graylog message contained no id nor index: $wrapper")
      return null
    }
    
    URI endpoint = new URIBuilder(config.webHost ?: config.connectionConfig.host)
      .setPath("/messages/${index}/${messageId}")
      .build()
    
    log.debug("Generated message reference for $wrapper: $endpoint")
    return endpoint
  }
}
