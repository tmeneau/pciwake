package com.xetus.pci.wake.manager.fail

import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@CompileStatic
@InheritConstructors
class TransientLogManagerQueryException extends RuntimeException {
  /*
   * Note: these constructors are only (evilly) to handle Eclipse confusion 
   * around the @InheritConstructors annotation
   */
  TransientLogManagerQueryException() {
    super()
  }
  
  TransientLogManagerQueryException(String message) {
    super(message)
  }
  
  TransientLogManagerQueryException(String message, Exception e) {
    super(message, e)
  }
}
