package com.xetus.pci.wake.review

import groovy.transform.CompileStatic

@CompileStatic
enum LogReviewStatusType {
  IN_PROGRESS,
  AWAITING_MANUAL_REVIEW,
  COMPLETED,
  FAILED_AWAITING_RETRY,
  FAILED
}
