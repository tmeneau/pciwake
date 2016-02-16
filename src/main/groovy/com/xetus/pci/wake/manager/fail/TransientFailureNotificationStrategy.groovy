package com.xetus.pci.wake.manager.fail

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.Table

import com.xetus.pci.wake.notification.Notification

@Entity
@Table(name = "transient_failure_notification_strategy")
@CompileStatic
@EqualsAndHashCode
class TransientFailureNotificationStrategy {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id
  
  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "transient_failure_notifications")
  List<Notification> failureNotifications = []
}
