package com.xetus.pci.wake.manager.fail

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * Represents a transient failure strategy, which is a configuration context 
 * object that exposes configuration and state management of transient failures. 
 * Because the {@link TransientFailureStrategy} instance manages state, there
 * should be one instance of a {@link TransientFailureStrategy} to the job 
 * configuration instance consuming the transient failure strategy.
 * 
 * <p>Note that while the Quartz library (which is currently being used
 * for scheduling recurrnig jobs) already has well-implemented failure 
 * handling, that failure handling does not reflect the failure or success of
 * the domain task to be completed by the running job; only whether or not the
 * job was executed. This class should be used to manually manage within the 
 * logic of the running class whether the job's (or the retried job's) 
 * completion of its task was a success or a failure.
 * 
 * <p>The following details seem confusing and may change given this class
 * being named a "Strategy", so it's possible either the name will change or 
 * the following features will:
 * 
 * <ul>
 *  <li>mixing model object references and (some very mild) domain logic
 *  <li>mixing model object references and state
 * </ul>
 * 
 * An alternative implementation strategy would be to make this a "support"
 * class (e.g. "TransientFailureSupport") and require consumers to <i>extend</i>
 * this class to inherit its state management and configurable model references.
 */
@Entity
@Table(name = "transient_failure_strategy")
@CompileStatic
@EqualsAndHashCode
class TransientFailureStrategy {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id
  
  @Column(name = "retry_attempts")
  Integer retryAttempts = 0
  
  @ManyToOne
  @JoinColumn(name = "job_recovery_strategy_id")
  TransientFailureJobRecoveryStrategy jobRecoveryStrategy
  
  @ManyToOne
  @JoinColumn(name = "notification_strategy_id")
  TransientFailureNotificationStrategy notificationStrategy
  
  /**
   * The date of the last successful run. This is intended for internal use
   * in evaluating the range of dates against which to query the log manager
   * instance.
   */
  @Column(name = "last_successful_run_date")
  Date lastSuccessfulRun
  
  /**
   * Registers a failed retry, handling any internal state management changes 
   * that need to be made.
   */
  void registerFailedRetry() {
    retryAttempts += 1
  }
  
  /**
   * Registers a surrender (not performing any more retries), which resets any
   * internal state that needs to be reset.
   */
  void registerSurrender() {
    retryAttempts = 0
  }
  
  /**
   * Registers a success, updating internal state to reflect 
   * @param fireTime
   */
  void registerSuccess(Date fireTime) {
    retryAttempts = 0
    lastSuccessfulRun = fireTime
  }
  
  /**
   * Convenience method to determine whether the job consuming the failure
   * strategy instance has 
   * 
   * @return
   */
  Boolean retryAttemptsExpired() {
    return jobRecoveryStrategy == null ||
           (jobRecoveryStrategy.retryLimit > -1 && 
            jobRecoveryStrategy.retryLimit <= retryAttempts)
  } 
}
