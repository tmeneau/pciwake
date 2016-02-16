package com.xetus.pci.wake.notification

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * A very simple structure to represent the state of a set of notification 
 * requests that failed for transient reasons. Note that this is for in-memory
 * purposes only and does not currently support persistence.
 */
@CompileStatic
@ToString
@EqualsAndHashCode
class QueuedNotificationRequest {
  Map<String, Object> binding
  List<Notification> notifications = []
  
  void add(Notification notification) {
    notifications << notification
  }
  
  void remove(Notification notification) {
    notifications.remove(notification)
  }
  
  void remove(List<Notification> notifications) {
    notifications.removeAll(notifications)
  }
  
  boolean empty() {
    return notifications.empty
  }
}
