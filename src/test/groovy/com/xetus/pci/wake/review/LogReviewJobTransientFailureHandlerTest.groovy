package com.xetus.pci.wake.review

import static com.xetus.pci.wake.review.DateUtils.getDate

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull

import static org.mockito.Matchers.any as anyMock
import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

import groovy.transform.CompileStatic

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters

import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

import com.xetus.pci.wake.manager.fail.TransientFailureJobRecoveryStrategy
import com.xetus.pci.wake.manager.fail.TransientFailureStrategy
import com.xetus.pci.wake.scheduler.SchedulerEngine

@CompileStatic
@RunWith(Parameterized.class)
class LogReviewJobTransientFailureHandlerTest {

  @Mock
  SchedulerEngine schedulerEngine
  
  @Captor
  ArgumentCaptor<Date> scheduledRetryCaptor
  
  @Mock
  LogReviewJobConfigRepository jobConfigRepo
  
  @Spy
  LogReviewJobTransientFailureHandler failureHandler
  
  @Rule
  public MockitoRule rule = MockitoJUnit.rule()
  
  @Parameter(0)
  public String testName
  
  @Parameter(1)
  public Date nextRetryTime
  
  @Parameter(2)
  public Date nextFireTime
  
  @Parameter(3)
  public TransientFailureStrategy failStrategy
  
  @Parameter(4)
  public Boolean expectedReschedule
  
  @Parameter(5)
  public LogReviewStatusType expectedResult
  
  @Parameter(6)
  public Integer expectedRetryCount
  
  @Before
  public void setup() {
    when(schedulerEngine.scheduleLogReviewRetry(
      (Date) anyMock(), (LogReviewJobConfig) anyMock()))
      .thenReturn(new Date())
    
    when(jobConfigRepo.save((LogReviewJobConfig) anyMock()))
      .thenReturn((LogReviewJobConfig) anyMock())
      
    failureHandler.jobConfigRepo = jobConfigRepo
    failureHandler.schedulerEngine = schedulerEngine
  }
  
  @Test
  public void test() {
    doReturn(nextRetryTime)
      .when(failureHandler)
        .getNextRetryTime((TransientFailureStrategy) anyMock())
    
    doReturn(jobConfigRepo)
      .when(failureHandler)
        .getJobConfigRepo()
        
    LogReviewJobConfig config = new LogReviewJobConfig(
      failureStrategy: failStrategy
    )
    
    LogReviewStatusType actualResult = failureHandler
      .handleFailure(config, nextFireTime, new Exception())
    
    assertEquals("Unexpected status type result", expectedResult, actualResult)
    assertEquals("unexpected failure strategy retry attempt count change",
                 expectedRetryCount, failStrategy?.retryAttempts)
    
    Date rescheduledDate
    try {
      verify(schedulerEngine).scheduleLogReviewRetry(
        scheduledRetryCaptor.capture(), (LogReviewJobConfig) anyMock())
      rescheduledDate = scheduledRetryCaptor.getValue()
    } catch (org.mockito.exceptions.verification.WantedButNotInvoked e) {
      assertFalse("Log review should have been rescheduled but was not",
                 expectedReschedule)
    }

    if (!expectedReschedule) {
      assertNull("log review was unexpectedly rescheduled", rescheduledDate)
    } else {
      assertEquals("unexpected retry date", nextRetryTime, rescheduledDate)
    }
  }
  
  @Parameters(name = "LogreviewJobTransientFailureHandlerTest {index}: {0}")
  public static List<Object[]> getParaeters() {
    List<Object[]> testCases = []
    
    TransientFailureJobRecoveryStrategy recoveryStrategy = 
      new TransientFailureJobRecoveryStrategy(retryLimit: 3)
    
    testCases << ([
      "failure handler schedules retry",
      getDate(2016, 02, 16, 9, 0, 0),
      getDate(2016, 02, 16, 20, 0, 0),
      new TransientFailureStrategy(
          retryAttempts: 0,
          jobRecoveryStrategy: recoveryStrategy
      ),
      true,
      LogReviewStatusType.FAILED_AWAITING_RETRY,
      1
    ] as Object[])
    
    testCases << ([
      "failure handler surrenders on expired retry attempts",
      getDate(2016, 02, 16, 9, 0, 0),
      getDate(2016, 02, 16, 20, 0, 0),
      new TransientFailureStrategy(
          retryAttempts: 3,
          jobRecoveryStrategy: recoveryStrategy
      ),
      false,
      LogReviewStatusType.FAILED,
      0
    ] as Object[])
    
    testCases << ([
      "failure handler surrenders when next retry attempt is before next " +
      "normal fire time",
      getDate(2016, 02, 16, 9, 0, 0),
      getDate(2016, 02, 16, 8, 30, 0),
      new TransientFailureStrategy(
          retryAttempts: 1,
          jobRecoveryStrategy: recoveryStrategy
      ),
      false,
      LogReviewStatusType.FAILED,
      0
    ] as Object[])
    
    testCases << ([
      "failure handler is null safe",
      getDate(2016, 02, 16, 9, 0, 0),
      getDate(2016, 02, 16, 8, 30, 0),
      null,
      false,
      LogReviewStatusType.FAILED,
      null
    ] as Object[])
    
    
    return testCases
  }
  
}