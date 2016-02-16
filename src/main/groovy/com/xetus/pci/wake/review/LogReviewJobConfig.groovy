package com.xetus.pci.wake.review

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.Table

import org.quartz.CronExpression

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.xetus.pci.wake.manager.LogManagerClientConfig
import com.xetus.pci.wake.manager.fail.TransientFailureStrategy
import com.xetus.pci.wake.notification.Notification
import com.xetus.pci.wake.persistence.CronExpressionConverter
import com.xetus.pci.wake.transfer.CronExpressionJsonSerializer

@Entity
@Table(name = "log_review_job_config")
@CompileStatic
@EqualsAndHashCode
class LogReviewJobConfig {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id
  
  String jobName
  
  String jobGroup
  
  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "no_incident_found_notifications")
  List<Notification> noIncidentFoundNotifications = []
  
  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "incident_found_notifications")
  List<Notification> incidentFoundNotifications = []
  
  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "review_resolved_notifications")
  List<Notification> reviewResolvedNotifications = []
  
  @OneToOne(cascade = [CascadeType.ALL])
  @JoinColumn(name = "transient_failure_strategy_id")
  TransientFailureStrategy failureStrategy
  
  @OneToOne
  @JoinColumn(name = "log_manager_client_config_id")
  LogManagerClientConfig logManagerClientConfig
  
  @Convert(converter = CronExpressionConverter.class)
  @JsonSerialize(using = CronExpressionJsonSerializer.class)
  @Column(name = "cron_expression")
  CronExpression frequency
  
}
