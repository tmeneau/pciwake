package com.xetus.pci.wake.review

import groovy.transform.CompileStatic

import static com.xetus.pci.wake.review.DateUtils.getDate

import static org.junit.Assert.assertEquals

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
import com.xetus.pci.wake.manager.fail.TransientLogManagerQueryException
import com.xetus.pci.wake.review.incident.LogIncident
import com.xetus.pci.wake.review.incident.LogIncidentStatusType

@CompileStatic
@RunWith(Parameterized.class)
class LogReviewJob_ReviewManagementTest extends LogReviewJobTestBase {
  
  @Captor
  public ArgumentCaptor<ExecutedLogReview> reviewCaptor
  
  @Parameter(0)
  public String testName
  
  @Parameter(1)
  public LogReviewJobConfig config
  
  @Parameter(2)
  public Date startDate
  
  @Parameter(3)
  public Date fireDate
  
  @Parameter(4)
  public LogQueryResult queryResult
  
  @Parameter(5)
  public Boolean throwException
  
  @Parameter(6)
  public LogReviewStatusType failureResultStatus
  
  @Parameter(7)
  public ExecutedLogReview expected
  
  
  
  public LogReviewJobConfig getConfig() {
    return this.config
  }
  
  public Date getStartDate() {
    return this.startDate
  }
  
  public Date getFireDate() {
    return this.fireDate
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
  
  @Override
  public LogReviewStatusType getFailureHandlerResultStatus() { 
    return this.failureResultStatus 
  }
  
  @Test
  public void test() {
    job.executeInternal(context)
    verify(reviewRepo, atLeastOnce()).save(reviewCaptor.capture())
    ExecutedLogReview actual = reviewCaptor.getValue()
    
    ObjectMapper om = new ObjectMapper()
    om.enable(SerializationFeature.INDENT_OUTPUT)
    
    println("test: $testName"
      + "\nexpected: " + om.writeValueAsString(expected) 
      + "\nactual:   " + om.writeValueAsString(actual) + "\n\n")
    
    assertEquals("unexpected log review persisted", expected, actual)
  }
  
  @Parameters(name = "LogReviewJob -- review generation test {index}: {0}")
  public static List<Object[]> getParameters() {
    List<Object[]> testCases = []
    
    LogReviewJobConfig jobConfig = new LogReviewJobConfig()
    Date startDate = getDate(2016, 2, 15, 9, 0, 0)
    Date endDate = getDate(2016, 2, 16, 9, 0, 0)
    
    testCases << ([
      "query result without incident generated completed review",
      jobConfig,
      startDate,
      endDate,
      new LogQueryResult(incidentUrls: []),
      false,
      null,
      new ExecutedLogReview(
        effectiveReviewStartTime: startDate,
        effectiveReviewEndTime: endDate,
        incidents: [],
        status: LogReviewStatusType.COMPLETED,
        jobConfig: jobConfig 
      )
    ] as Object[])
    
    testCases << ([
      "query result with incident generates review requiring manual intervention",
      jobConfig,
      startDate,
      endDate,
      new LogQueryResult(incidentUrls: [new URI("http://www.test.com")]),
      false,
      null,
      new ExecutedLogReview(
        effectiveReviewStartTime: startDate,
        effectiveReviewEndTime: endDate,
        incidents: [new LogIncident(
          reference: new URI("http://www.test.com"),
          status: LogIncidentStatusType.NOT_REVIEWED
        )],
        status: LogReviewStatusType.AWAITING_MANUAL_REVIEW,
        jobConfig: jobConfig 
      )
    ] as Object[])
    
    testCases << ([
      "review is persisted with failure handler status result: FAILED_AWAITING_RETRY",
      jobConfig,
      startDate,
      endDate,
      null,
      true,
      LogReviewStatusType.FAILED_AWAITING_RETRY,
      new ExecutedLogReview(
        effectiveReviewStartTime: startDate,
        effectiveReviewEndTime: endDate,
        status: LogReviewStatusType.FAILED_AWAITING_RETRY,
        jobConfig: jobConfig
      )
    ] as Object[])
    
    testCases << ([
      "review is persisted with failure handler status result: FAILED",
      jobConfig,
      startDate,
      endDate,
      null,
      true,
      LogReviewStatusType.FAILED,
      new ExecutedLogReview(
        effectiveReviewStartTime: startDate,
        effectiveReviewEndTime: endDate,
        status: LogReviewStatusType.FAILED,
        jobConfig: jobConfig
      )
    ] as Object[])
    
    return testCases
  }
  
}
