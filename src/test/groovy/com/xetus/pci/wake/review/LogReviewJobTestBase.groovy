package com.xetus.pci.wake.review

import groovy.transform.CompileStatic
import static org.mockito.AdditionalAnswers.returnsFirstArg
import static org.mockito.Mockito.any as anyMock
import static org.mockito.Mockito.doNothing
import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.when

import org.junit.Before
import org.junit.Rule
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.quartz.JobExecutionContext
import org.quartz.Trigger;

import com.xetus.pci.wake.manager.LogManagerClientConfig
import com.xetus.pci.wake.manager.LogManagerQueryService
import com.xetus.pci.wake.manager.LogQueryResult
import com.xetus.pci.wake.manager.fail.TransientFailureStrategy
import com.xetus.pci.wake.notification.Notification
import com.xetus.pci.wake.notification.NotificationService

@CompileStatic
abstract class LogReviewJobTestBase {

  @Mock
  public LogReviewJobConfigRepository jobConfigRepo
  
  @Mock
  public LogReviewJobQueryStartTimeResolutionService startTimeResolutionService
  
  @Mock
  public LogReviewJobTransientFailureHandler failureHandler
  
  @Mock
  public LogManagerQueryService queryService
  
  @Mock
  public NotificationService notificationService
  
  @Mock
  public ExecutedLogReviewRepository reviewRepo
  
  @Mock
  public JobExecutionContext context
  
  @Mock
  public Trigger trigger
  
  @Rule
  public MockitoRule rule = MockitoJUnit.rule()
  
  @Spy
  public LogReviewJob job
 
  /*
   * required objects to return from the mock / spied service instances
   */
  
  public abstract Date getStartDate()
  public abstract Date getFireDate()
  public abstract LogReviewJobConfig getConfig()
  public abstract LogQueryResult getQueryResult()
  
  /*
   * optional objects to return / throw from the mock service instances
   */
  
  public ExecutedLogReview getReviewAwaitingRetry() { return null }
  public LogReviewStatusType getFailureHandlerResultStatus() { return null }
  public Exception getQueryException() { return null }

  
  @Before
  public void setup() {
    when(context.getTrigger()).thenReturn(trigger)
    when(trigger.getNextFireTime()).thenReturn(null)
    
    doReturn(config)
      .when(job)
        .getJobConfig((JobExecutionContext) anyMock())
    
    doReturn(fireDate)
      .when(job)
        .getEndTime()
    
    when(startTimeResolutionService.getStartDate(
      (TransientFailureStrategy) anyMock(), 
      (JobExecutionContext) anyMock()
    )).thenReturn(startDate)
        
    when(reviewRepo.findFirstByJobConfigAndStatus(
      (LogReviewJobConfig) anyMock(), 
      (LogReviewStatusType) anyMock())
    ).thenReturn(reviewAwaitingRetry)
    
    when(reviewRepo.save((ExecutedLogReview) anyMock())).then(returnsFirstArg())
    
    if (queryException) {
      when(queryService.query(
        (LogManagerClientConfig) anyMock(), 
        (Date) anyMock(), 
        (Date) anyMock())
      ).thenThrow(queryException)
    } else {
      when(queryService.query(
        (LogManagerClientConfig) anyMock(), 
        (Date) anyMock(), 
        (Date) anyMock())
      ).thenReturn(queryResult)
    }
    
    when(failureHandler.handleFailure(
      (LogReviewJobConfig) anyMock(), (Date) anyMock(), (Exception) anyMock())
    ).thenReturn(failureHandlerResultStatus)

    when(jobConfigRepo.save((LogReviewJobConfig) anyMock()))
      .then(returnsFirstArg())
        
    doNothing()
      .when(notificationService)
        .notify((List<Notification>) anyMock(), (Map<String, Object>) anyMock())
    
    job.jobConfigRepo = jobConfigRepo
    job.startTimeResolutionService = startTimeResolutionService
    job.failureHandler = failureHandler
    job.queryService = queryService
    job.notificationService = notificationService
    job.reviewRepo = reviewRepo
  }
  
}
