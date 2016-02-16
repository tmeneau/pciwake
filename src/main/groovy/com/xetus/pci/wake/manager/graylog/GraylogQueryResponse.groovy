package com.xetus.pci.wake.manager.graylog

import groovy.transform.CompileStatic

import com.fasterxml.jackson.annotation.JsonProperty

@CompileStatic
class GraylogQueryResponse {

  @JsonProperty("type")
  String errorType
  
  @JsonProperty("message")
  String errorMessage
  
  @JsonProperty("messages")
  List<GraylogQueryResultMessageWrapper> results
  
}
