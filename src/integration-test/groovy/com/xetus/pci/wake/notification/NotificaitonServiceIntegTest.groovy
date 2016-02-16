package com.xetus.pci.wake.notification

import groovy.transform.CompileStatic

import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail

import javax.inject.Inject

import org.junit.After
import org.junit.Test

import com.xetus.pci.wake.AbstractNoSchedulerSpringContextTest

@CompileStatic
class NotificaitonServiceIntegTest extends AbstractNoSchedulerSpringContextTest {
  
  @Inject
  NotificationService notificationService
  
  @Inject
  FirstTestNotificationProcessor firstProcessor
  
  @Inject
  SecondTestNotificationProcessor secondProcessor
  
  @After
  void cleanup() {
    firstProcessor.receivedNotifications = []
    secondProcessor.receivedNotifications = []
  }
  
  @Test
  void testNotificationServiceNotifiesCorrectPlugin() {
    FirstTestNotification first = new FirstTestNotification()
    
    try {
      notificationService.notify([(Notification) first], [:])
    } catch(IllegalArgumentException e) {
      fail(e.message)
    }
    assertEquals("Expected first processor to receive one notification",
      1, firstProcessor.receivedNotifications.size())
    assertEquals("Expected first processor to receive first notification",
      first, firstProcessor.receivedNotifications[0])
    
    assertEquals("Expected second processor to receive no notifications",
      0, secondProcessor.receivedNotifications.size())
    
  }
  
  @Test
  void testNotificationServiceCorrectlyNotifiesMultiplePlugins() {
    FirstTestNotification first = new FirstTestNotification()
    SecondTestNotification second = new SecondTestNotification()
    
    try {
      notificationService.notify([first, second], [:])
    } catch(IllegalArgumentException e) {
      fail(e.message)
    }
    assertEquals("Expected first processor to receive one notification",
      1, firstProcessor.receivedNotifications.size())
    assertEquals("Expected first processor to receive first notification",
      first, firstProcessor.receivedNotifications[0])
    
    assertEquals("Expected second processor to receive one notifications",
      1, secondProcessor.receivedNotifications.size())
    
    assertEquals("Expected second processor to receive second notifications",
      second, secondProcessor.receivedNotifications[0])
  }

}
