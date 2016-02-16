package com.xetus.pci.wake.review.incident

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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.xetus.pci.wake.review.ExecutedLogReview

@Entity
@Table(name = "log_incident")
@CompileStatic
@EqualsAndHashCode(excludes = "review")
class LogIncident {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id
  
  @ManyToOne
  @JoinColumn(name = "log_review_id")
  @JsonIgnore
  ExecutedLogReview review
  
  @JsonIgnore
  public ExecutedLogReview getReview() {
    return this.review
  }
  
  @Column(unique = true)
  URI reference
  
  @Column
  LogIncidentStatusType status
  
  @Column(name = "status_message")
  String statusMessage
  
  @Column
  String reviewer
}
