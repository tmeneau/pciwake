package com.xetus.pci.wake.notification

import groovy.transform.CompileStatic

import javax.persistence.DiscriminatorColumn
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Inheritance
import javax.persistence.InheritanceType
import javax.persistence.Table

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id as JsonId

/**
 * A simple base class for representing a notification. Recommended subclass
 * implementations should define a `messageTemplate` field and any configuration
 * fields required for actually delivering the notification. 
 */
@CompileStatic
@Entity
@Table(name = "notification")
@DiscriminatorColumn(name = "notification_type")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use=JsonId.CLASS, property="_class")
class Notification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id
  
  String name
}
