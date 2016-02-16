package com.xetus.pci.wake.manager

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@CompileStatic
@EqualsAndHashCode
class LogQueryResult {
  Long id  
  List<URI> incidentUrls
  URI requestEndpoint
}
