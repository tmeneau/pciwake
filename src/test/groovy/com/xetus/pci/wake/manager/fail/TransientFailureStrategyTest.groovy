package com.xetus.pci.wake.manager.fail

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

import groovy.transform.CompileStatic

import org.junit.Test

@CompileStatic
class TransientFailureStrategyTest {

  @Test
  void testFailureRegistryIncrementsRetryAttempts() {
    def failStrategy = new TransientFailureStrategy(
      retryAttempts: 0 
    )
    failStrategy.registerFailedRetry()
    
    assertEquals("expected retry attempt to increment with retry failure",
      1, failStrategy.retryAttempts)
  }
  
  @Test
  void testSurrenderRegistryResetsRetryAttempts() {
    def failStrategy = new TransientFailureStrategy(
      retryAttempts: 1
    )
    failStrategy.registerSurrender()
    
    assertEquals("expected retry attempts to be reset on surrender",
      0, failStrategy.retryAttempts)
  }
  
  @Test
  void testSuccessRegistryUpdatesSuccssefulRunAndResetsRetryAttempts() {
    def failStrategy = new TransientFailureStrategy(
      retryAttempts: 1
    )
    Date mockFireTime = new Date()
    failStrategy.registerSuccess(mockFireTime)
    
    assertEquals("expected retry attempts to be reset on success",
      0, failStrategy.retryAttempts)
    
    assertEquals("expected last successful run to be set to success fire time",
      mockFireTime, failStrategy.lastSuccessfulRun)
  }
  
  @Test
  void testRetryAttemptsExpiredReflectsState() {
    def failStrategy = new TransientFailureStrategy(
      retryAttempts: 0,
      jobRecoveryStrategy: new TransientFailureJobRecoveryStrategy(
        retryLimit: 3 
      )
    )
    assertFalse("did not expect expired retry attempts with 0 retry attempts "
      + "and retry limit of 3", failStrategy.retryAttemptsExpired())
    
    failStrategy.retryAttempts = 3
    assertTrue("expected expired retry attempts with 3 retry attempts and "
      + "retry limit of 3", failStrategy.retryAttemptsExpired())
    
    failStrategy.retryAttempts = 5
    assertTrue("expected expired retry attempts with 5 retry attempts and "
      + "retry limit of 3", failStrategy.retryAttemptsExpired())
  }
  
  @Test
  void testRetryAttemptsExpiredIsNullSafe() {
    def failStrategy = new TransientFailureStrategy(
      retryAttempts: 0,
      jobRecoveryStrategy: (TransientFailureJobRecoveryStrategy) null
    )
    
    assertTrue("expected expired retry attempts with 0 retry attempts and "
      + "null job recovery strategy", failStrategy.retryAttemptsExpired())
    
    /*
     * not that the following should ever happen....
     */
    
    failStrategy.retryAttempts = -1000
    assertTrue("expected expired retry attempts with -1000 retry attempts and "
      + "null job recovery strategy", failStrategy.retryAttemptsExpired())
    
    failStrategy.retryAttempts = 1000
    assertTrue("expected expired retry attempts with 1000 retry attempts and "
      + "null job recovery strategy", failStrategy.retryAttemptsExpired())
  }
  
  @Test
  void testNegativeRetryLimitsAllowsInfiniteRetries() {
    def failStrategy = new TransientFailureStrategy(
      retryAttempts: 0,
      jobRecoveryStrategy: new TransientFailureJobRecoveryStrategy(
        retryLimit: -1
      )
    )
    
    assertFalse("did not expect expired retry attempts with 0 retry attempts "
      + "and retry limit of 3", failStrategy.retryAttemptsExpired())
    
    failStrategy.retryAttempts = 9000
    assertFalse("did not expect expired retry attempts with 9000 retry "
      + "attempts  and retry limit of -1", failStrategy.retryAttemptsExpired())
  }
  
  @Test
  void testFailureAPIIntegratesAsExpected() {
    def failStrategy = new TransientFailureStrategy(
      retryAttempts: 0,
      jobRecoveryStrategy: new TransientFailureJobRecoveryStrategy(
        retryLimit: 3
      )
    )
    
    
  }
  
}
