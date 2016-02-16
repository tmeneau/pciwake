package com.xetus.pci.wake.review

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

import com.xetus.pci.wake.review.incident.LogIncident
import com.xetus.pci.wake.review.incident.LogIncidentStatusType


/**
 * The representation of a triggered log review and the entry point for 
 * managing the manual 
 */
@Entity
@Table(name = "log_review")
@ToString
@CompileStatic
@EqualsAndHashCode
class ExecutedLogReview {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id
  
  @Lob
  @Column
  String reviewerMessage
  
  @Column
  LogReviewStatusType status
  
  @ManyToOne
  @JoinColumn(name = "job_config_id")
  LogReviewJobConfig jobConfig
  
  @OneToMany(
    mappedBy = "review", 
    cascade = CascadeType.ALL, 
    fetch = FetchType.EAGER
  )
  List<LogIncident> incidents
  
  @Column(name = "effective_review_start_time")
  Date effectiveReviewStartTime
  
  @Column(name = "effective_review_end_time")
  Date effectiveReviewEndTime
  
  /**
   * Updates the incident in the {@link #incidents} corresponding to the
   * supplied <code>incidentId</code> (the persisted incident id) with the 
   * values supplied for the reviewer, status, and status message. 
   * 
   * @param incidentId the persistent id corresponding to the incident to update
   * @param reviewer the reviewer that should be configured for the incident
   * @param status the {@link LogIncidentStatusType} that should be configured
   * for the incident
   * @param message the status message that should be configured for the 
   * incident
   * 
   * @return the updated incident
   */
  public LogIncident updateIncident(Long incidentId,
                                    String reviewer, 
                                    LogIncidentStatusType status, 
                                    String message) {
    LogIncident incident = incidents.find { it.id == incidentId }
    if (incident == null) {
      throw new IllegalArgumentException("Invalid incident id $incidentId for "
        + "review with id $id")
    }
    
    incident.reviewer = reviewer
    incident.status = status
    incident.statusMessage = message
    
    if (incidents.find { 
          LogIncident it -> it.status != LogIncidentStatusType.REVIEWED
        }) {
      this.status = LogReviewStatusType.AWAITING_MANUAL_REVIEW 
    } else {
      this.status = LogReviewStatusType.COMPLETED
    }
    
    return incident
  }
}
