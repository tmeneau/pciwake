package com.xetus.pci.wake.manager

import groovy.transform.CompileStatic

import org.springframework.stereotype.Component

@Component
@CompileStatic
class FirstTestLogManagerClientPlugin 
      extends AbstractLogManagerClientPlugin<FirstLogManagerClientConfig> {
  static final String RESULT_TEXT = "FIRST_RESULT_TEXT"
  static final LogQueryResult LOG_RESULT = new LogQueryResult(
    incidentUrls: [new URI("http://www.test.com")] 
  )
  
  @Override
  public String getResultText(FirstLogManagerClientConfig config, URI endpoint) {
    RESULT_TEXT
  }

  @Override
  public LogQueryResult getLogs(FirstLogManagerClientConfig config,
                                Date startTime, Date endTime) {
    LOG_RESULT
  }
}