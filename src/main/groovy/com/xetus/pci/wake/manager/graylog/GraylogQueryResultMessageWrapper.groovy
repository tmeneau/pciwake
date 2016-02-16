package com.xetus.pci.wake.manager.graylog

import groovy.transform.CompileStatic

@CompileStatic
class GraylogQueryResultMessageWrapper {
  String index
  GraylogQueryResultMessage message
}
