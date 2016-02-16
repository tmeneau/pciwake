package com.xetus.pci.wake.scheduler

import groovy.transform.CompileStatic

@CompileStatic
class SchedulerSubmissionResult {
  SchedulerSubmissionStatus status
  String message
  Long submittedConfigId
  Long duplicatedConfigId
}
