package com.xetus.pci.wake.notification

import groovy.transform.CompileStatic

import org.junit.After
import org.springframework.stereotype.Component

/**
 * A notification processor for use in testing the PCI Wake notification 
 * framework. Note that this exposes a few stateful fields that require
 * the consumer performs some manual clean up after use; for example:
 * 
 * 
 * @Inject
 * FirstTestNotificationProcessor notificationProcessor
 * 
 * @After
 * public void cleanUp() {
 *   notificationProcessor.injectedLogic = null
 *   notificationProcessor.receivedNotifications = []
 * }
 * 
 * @Test
 * public void test() {
 *  notificationProcessor.injectedLogic = { 
 *    // your logic here  
 *  }
 *  
 *  notificationProcessor.process(notifications, [:])
 *  // assertions...
 * }
 */
@Component
@CompileStatic
class FirstTestNotificationProcessor
      extends AbstractNotificationProcessorPlugin<FirstTestNotification> {

  List<FirstTestNotification> receivedNotifications = []
  
  /**
   * A closure allowing consuming test classes to inject their own logic 
   * (including throwing specific exception types for specific notification
   * instances).
   * 
   * Note: consummers are responsible for cleaning up this field after use!
   */
  Closure injectedLogic
  
  @After
  public void cleanup() {
    injectedLogic = null
    receivedNotifications = []
  }
  
  @Override
  public void process(FirstTestNotification notification, Map dataBinding)
      throws TransientNotificationException {
    receivedNotifications << notification
    
    if (injectedLogic != null) {
      try {
        injectedLogic(notification, dataBinding)
      } catch(e) {
        throw e
      }
    }
    
  }

}
