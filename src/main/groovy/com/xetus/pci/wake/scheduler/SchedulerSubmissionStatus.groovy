package com.xetus.pci.wake.scheduler

import groovy.transform.CompileStatic

@CompileStatic
enum SchedulerSubmissionStatus {
  SUCCESS("Successfully submitted job"),
  ALREADY_SCHEDULED("Job has already been scheduled"),
  INVALID_ID("No configuration exists with the supplied ID"),
  ERROR("There was an error scheduling a job for the supplied configuration")
  
  String message
  
  public SchedulerSubmissionStatus(String message) {
    this.message = message
  }
}
