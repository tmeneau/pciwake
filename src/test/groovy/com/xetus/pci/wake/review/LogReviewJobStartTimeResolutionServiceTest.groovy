package com.xetus.pci.wake.review

import java.util.GregorianCalendar

import groovy.transform.CompileStatic
import static org.junit.Assert.assertEquals

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import org.mockito.Mockito

import static org.mockito.Mockito.when

import org.quartz.JobExecutionContext
import org.quartz.Trigger

import com.xetus.pci.wake.manager.fail.TransientFailureStrategy
import static com.xetus.pci.wake.review.DateUtils.getDate

@RunWith(Parameterized.class)
@CompileStatic
class LogReviewJobStartTimeResolutionServiceTest {

  @Parameter(0)
  public String testName
  
  @Parameter(1)
  public TransientFailureStrategy failStrategy
  
  @Parameter(2)
  public JobExecutionContext context
  
  @Parameter(3)
  public Date expected
  
  @Test
  public void test() {
    LogReviewJobQueryStartTimeResolutionService resolutionService = 
      new LogReviewJobQueryStartTimeResolutionService()
      
    Date actual = resolutionService.getStartDate(failStrategy, context)
    assertEquals("Unexpected actual date", expected, actual)
  }
  
  public static JobExecutionContext createContext(ContextConfigurator config) {
    JobExecutionContext context = Mockito.mock(JobExecutionContext.class)
    Trigger trigger = Mockito.mock(Trigger.class)
    
    when(trigger.getNextFireTime()).thenReturn(config.nextFireTime)
    when(context.getTrigger()).thenReturn(trigger)
    
    when(context.getFireTime()).thenReturn(config.fireTime)
    when(context.getPreviousFireTime()).thenReturn(config.previousFireTime)
    
    return context
  } 
  
  @Parameters(name = "LogReviewJobStartTimeResolutionServiceTest {index}: {0}")
  public static List<Object[]> getParameters() {
    List<Object[]> testCases = []
    
    testCases << ([
      "previous fire time is the start date",
      new TransientFailureStrategy(),
      createContext(new ContextConfigurator(
        previousFireTime: getDate(2016, 02, 15, 10, 15, 00), 
        fireTime: new Date(), 
        nextFireTime: new Date()
      )),
      getDate(2016, 02, 15, 10, 15, 00)
    ] as Object[])
    
    testCases << ([
      "previous fire time computed from difference between fire and next fire",
      new TransientFailureStrategy(),
      createContext(new ContextConfigurator( 
        fireTime: getDate(2016, 02, 16, 10, 15, 00), 
        nextFireTime: getDate(2016, 02, 17, 10, 15, 00)
      )),
      getDate(2016, 02, 15, 10, 15, 00)
    ] as Object[])
    
    testCases << ([
      "previous fire time overriden to last successful run if computed previous fire time",
      new TransientFailureStrategy(lastSuccessfulRun: getDate(2015, 01, 01, 01, 01, 01)),
      createContext(new ContextConfigurator(
        fireTime: getDate(2016, 02, 16, 10, 15, 00),
        nextFireTime: getDate(2016, 02, 17, 10, 15, 00)
      )),
      getDate(2015, 01, 01, 01, 01, 01)
    ] as Object[])
    
    testCases << ([
      "previous fire time overriden to last successful run if previous fire time",
      new TransientFailureStrategy(lastSuccessfulRun: getDate(2015, 01, 01, 01, 01, 01)),
      createContext(new ContextConfigurator(
        previousFireTime: getDate(2016, 02, 15, 10, 15, 00),
        fireTime: getDate(2016, 02, 16, 10, 15, 00),
        nextFireTime: getDate(2016, 02, 17, 10, 15, 00)
      )),
      getDate(2015, 01, 01, 01, 01, 01)
    ] as Object[])
    
    testCases << ([
      "previous fire time not overriden to last successful run if last success sooner than previous fire time",
      new TransientFailureStrategy(lastSuccessfulRun: getDate(2016, 02, 16, 01, 01, 01)),
      createContext(new ContextConfigurator(
        previousFireTime: getDate(2016, 02, 15, 10, 15, 00),
        fireTime: getDate(2016, 02, 16, 10, 15, 00),
        nextFireTime: getDate(2016, 02, 17, 10, 15, 00)
      )),
      getDate(2016, 02, 15, 10, 15, 00)
    ] as Object[])
    
    testCases << ([
      "computed fire time not overriden to last successful run if last success sooner than computed time",
      new TransientFailureStrategy(lastSuccessfulRun: getDate(2016, 02, 16, 01, 01, 01)),
      createContext(new ContextConfigurator(
        fireTime: getDate(2016, 02, 16, 10, 15, 00),
        nextFireTime: getDate(2016, 02, 17, 10, 15, 00)
      )),
      getDate(2016, 02, 15, 10, 15, 00)
    ] as Object[])
    
    return testCases
  }
  
}

class ContextConfigurator {
  Date previousFireTime
  Date fireTime
  Date nextFireTime
}
