package com.xetus.pci.wake.notification

import groovy.transform.CompileStatic

import java.util.concurrent.CopyOnWriteArrayList

import javax.inject.Inject

import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters

import com.xetus.pci.wake.AbstractParameterizedNoSChedulerSpringContextTest

@CompileStatic
class NotificationRetryJobIntegTest extends AbstractParameterizedNoSChedulerSpringContextTest {
  
  @Inject
  NotificationRetryJob retryJob
  
  @Inject 
  NotificationService notificationService
  
  @Inject
  FirstTestNotificationProcessor firstProcessor
  
  @Parameter(0)
  public String testName
  
  @Parameter(1)
  public List<QueuedNotificationRequest> startQueue
  
  @Parameter(2)
  public Closure injectedProcessorLogic
  
  @Parameter(3)
  public List<QueuedNotificationRequest> expectedQueue
  
  @After
  public void cleanup() {
    firstProcessor.injectedLogic = null
    firstProcessor.receivedNotifications = []
    notificationService.queuedNotifications = new CopyOnWriteArrayList()
  }
  
  @Test
  public void test() {
    notificationService.queuedNotifications = startQueue
    firstProcessor.injectedLogic = injectedProcessorLogic
    
    retryJob.executeInternal(null)
    
    println "testName: $testName, \n" +
            "expected: $expectedQueue, \n" +
            "actual: ${notificationService.queuedNotifications}\n\n"
    
    Assert.assertArrayEquals( 
      expectedQueue.toArray(),
      notificationService.queuedNotifications.toArray()
    )
  }
  
  public static QueuedNotificationRequest getQueuedRequest(String id, Map<String, Object> binding) {
    return new QueuedNotificationRequest(
      notifications: [(Notification) new FirstTestNotification(testId: id)],
      binding: binding
    )
  }
  
  /**
   * Convenience method to ensure modification-safe queue
   * @param notifications
   * @return
   */
  public static List<QueuedNotificationRequest> getQueue(List<QueuedNotificationRequest> notifications) {
    return notifications as CopyOnWriteArrayList<QueuedNotificationRequest>
  }
  
  @Parameters(name = "NotificationRetryJobTest {index}: {0}")
  public static List<Object[]> getParameters() {
    List<Object[]> testCases = []
    
    Map<String, Object> binding = [:]
    QueuedNotificationRequest first = getQueuedRequest("first", binding)
    QueuedNotificationRequest second = getQueuedRequest("second", binding)
    QueuedNotificationRequest third = getQueuedRequest("third", binding)
    
    testCases << ([
      "empty queue remains empty",
      [],
      { Notification n, Map<String, Object> b -> },
      []
    ] as Object[])
    
    testCases << ([
      "transient exception on retry retains same queue",
      getQueue([first]),
      { Notification n, Map<String, Object> b -> throw new TransientNotificationException() },
      getQueue([first])
    ] as Object[])
    
    testCases << ([
      "successful retry removes notification from queue",
      getQueue([first]),
      { Notification n, Map<String, Object> b -> },
      getQueue([])
    ] as Object[])
    
    testCases << ([
      "non-transient exception on retry removes item from queue",
      getQueue([first]),
      { Notification n, Map<String, Object> b -> throw new RuntimeException() },
      getQueue([])
    ] as Object[])
    
    testCases << ([
      "non-transient exception on retry only removes non-transient item from queue",
      getQueue([first, second, third]),
      { Notification notification, Map<String, Object> b ->
        if (notification == second.notifications[0]) {
          throw new RuntimeException()
        }
        throw new TransientNotificationException()
      },
      getQueue([first, third])
    ] as Object[])
    
    testCases << ([
      "transient exception on retry only retains transient item in queue",
      getQueue([first, second, third]),
      { Notification notification, Map<String, Object> b ->
        if (notification == second.notifications[0]) {
          throw new TransientNotificationException()
        }
      },
      getQueue([second])
    ] as Object[])
    
    return testCases
  }
  
}
