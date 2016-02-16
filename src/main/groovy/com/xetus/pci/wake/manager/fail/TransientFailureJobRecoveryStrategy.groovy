package com.xetus.pci.wake.manager.fail

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode

import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

import org.quartz.CronExpression

import com.xetus.pci.wake.persistence.CronExpressionConverter

@Entity
@Table(name = "transient_failure_job_recovery_strategy")
@CompileStatic
@EqualsAndHashCode
class TransientFailureJobRecoveryStrategy {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id
  
  /**
   * Defines the interval that the Quartz scheduler should wait prior to
   * re-attempting the job. This is dependent on the {@link #retryAttemptCount}
   * being greater than or less than zero
   */
  @Column(name = "retry_interval")
  @Convert(converter = CronExpressionConverter.class)
  CronExpression retryInterval
  
  /**
   * Defines the number of retries that should be attempted prior to failing.
   * Any amount less than 0 indicates infinite retry attempts. 
   */
  @Column(name = "retry_limit")
  Integer retryLimit
  
}
