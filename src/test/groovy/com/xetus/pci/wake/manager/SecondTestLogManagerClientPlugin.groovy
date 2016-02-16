package com.xetus.pci.wake.manager

import groovy.transform.CompileStatic

import org.springframework.stereotype.Component

@Component
@CompileStatic
class SecondTestLogManagerClientPlugin 
      extends AbstractLogManagerClientPlugin<SecondLogManagerClientConfig> {
  static final String RESULT_TEXT = "SECOND_RESULT_TEXT"
  static final LogQueryResult LOG_RESULT = new LogQueryResult(
    incidentUrls: [new URI("http://www.google.com")] 
  )
  
  @Override
  public String getResultText(SecondLogManagerClientConfig config, 
                              URI endpoint) {
    RESULT_TEXT
  }

  @Override
  public LogQueryResult getLogs(SecondLogManagerClientConfig config,
                                Date startTime, Date endTime) {
    LOG_RESULT
  }
}