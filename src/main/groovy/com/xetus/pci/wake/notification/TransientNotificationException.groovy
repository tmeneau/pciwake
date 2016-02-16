package com.xetus.pci.wake.notification

import groovy.transform.InheritConstructors

/**
 * An exception base class that should be thrown by {@link 
 * NotificationProcessorPlugin}s whenever a transient exception restricts them
 * from delivering notifications.
 */
@InheritConstructors
class TransientNotificationException extends RuntimeException {
  
  TransientNotificationException(String message, Exception e) {
    super(message, e)
  }
  
  TransientNotificationException(Exception e) {
    super(e)
  }
  
  TransientNotificationException() {
    super()
  }
}
