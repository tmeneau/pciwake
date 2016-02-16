package com.xetus.pci.wake.review

import groovy.transform.CompileStatic

import static com.xetus.pci.wake.review.DateUtils.getDate
import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.any as anyMock
import static org.mockito.Mockito.atLeastOnce
import static org.mockito.Mockito.verify

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import org.mockito.ArgumentCaptor
import org.mockito.Captor

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.xetus.pci.wake.manager.LogQueryResult
import com.xetus.pci.wake.manager.fail.TransientFailureNotificationStrategy
import com.xetus.pci.wake.manager.fail.TransientFailureStrategy
import com.xetus.pci.wake.manager.fail.TransientLogManagerQueryException
import com.xetus.pci.wake.notification.Notification
import com.xetus.pci.wake.notification.mail.MailNotification

@CompileStatic
@RunWith(Parameterized.class)
class LogReviewJob_NotificaitonManagementTest extends LogReviewJobTestBase {
  
  @Captor
  ArgumentCaptor<List<Notification>> notificationsCaptor
  
  @Parameter(0)
  public String testName
  
  @Parameter(1)
  public LogReviewJobConfig config
  
  @Parameter(2)
  public LogQueryResult queryResult
  
  @Parameter(3)
  public Boolean throwException
  
  @Parameter(4)
  public List<Notification> expected
  
  public LogReviewJobConfig getConfig() {
    return this.config
  }
  
  public Date getStartDate() {
    return getDate(2016, 2, 15, 9, 0, 0)
  }
  
  public Date getFireDate() {
    return getDate(2016, 2, 16, 9, 0, 0)
  }
  
  public LogQueryResult getQueryResult() {
    return this.queryResult
  }
  
  @Override
  public Exception getQueryException() {
    if (throwException) {
      return (Exception) new TransientLogManagerQueryException()
    }
    return null
  }
  
  @Test
  public void test() {
    job.executeInternal(context)
    verify(notificationService, atLeastOnce())
      .notify(notificationsCaptor.capture(), (Map) anyMock())
    
    List<Notification> actual = notificationsCaptor.getValue()
    
    ObjectMapper om = new ObjectMapper()
    om.enable(SerializationFeature.INDENT_OUTPUT)
    
    println("test: $testName"
      + "\nexpected: " + om.writeValueAsString(expected)
      + "\nactual:   " + om.writeValueAsString(actual) + "\n\n")
    
    assertEquals("unexpected notifications", expected, actual)
  }
  
  @Parameters(name = "LogReviewJob -- notification test {index}: {0}")
  public static List<Object[]> getParameters() {
    List<Object[]> testCases = []
    
    List<Notification> expectedNotifications = [
      (Notification) new MailNotification(id: 1L, messageTemplate: "expected")
    ]
    
    testCases << ([
      "no incident found notifications sent when query result contains no incidents",
      new LogReviewJobConfig(
        noIncidentFoundNotifications: expectedNotifications,
        incidentFoundNotifications: [new Notification(id: 2L)],
        reviewResolvedNotifications: [new Notification(id: 3L)]
      ),
      new LogQueryResult(incidentUrls: []),
      false,
      expectedNotifications
    ] as Object[])
    
    expectedNotifications = [
      (Notification) new MailNotification(id: 1L, messageTemplate: "expected")
    ]
    testCases << ([
      "incident found notifications sent when query result contains incidents",
      new LogReviewJobConfig(
        noIncidentFoundNotifications: [new Notification(id: 2L)],
        incidentFoundNotifications: expectedNotifications,
        reviewResolvedNotifications: [new Notification(id: 3L)]
      ),
      new LogQueryResult(incidentUrls: [new URI("http://www.test.com")]),
      false,
      expectedNotifications
    ] as Object[])
    
    expectedNotifications = [
      (Notification) new MailNotification(id: 1L, messageTemplate: "expected")
    ]
    testCases << ([
      "failureStrategy.notificationStrategy.failureNotifications sent when transient query error is triggered",
      new LogReviewJobConfig(
        noIncidentFoundNotifications: [new Notification(id: 2L)],
        incidentFoundNotifications: [new Notification(id: 3L)],
        reviewResolvedNotifications: [new Notification(id: 4L)],
        failureStrategy: new TransientFailureStrategy(
          notificationStrategy: new TransientFailureNotificationStrategy(
            failureNotifications: expectedNotifications
          )
        )
      ),
      new LogQueryResult(incidentUrls: []),
      true,
      expectedNotifications
    ] as Object[])
    
    return testCases
  }
}
