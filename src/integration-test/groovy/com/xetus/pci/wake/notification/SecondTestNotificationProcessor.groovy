package com.xetus.pci.wake.notification

import groovy.transform.CompileStatic

import org.springframework.stereotype.Component

@Component
@CompileStatic
class SecondTestNotificationProcessor 
      extends AbstractNotificationProcessorPlugin<SecondTestNotification> {

  List<SecondTestNotification> receivedNotifications = []
  
  @Override
  public void process(SecondTestNotification notification, Map dataBinding)
      throws TransientNotificationException {
    receivedNotifications << notification
  }

}
