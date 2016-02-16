package com.xetus.pci.wake.review

import groovy.transform.CompileStatic

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

@CompileStatic
interface ExecutedLogReviewRepository extends JpaRepository<ExecutedLogReview, Long>,
                                              JpaSpecificationExecutor {
  ExecutedLogReview findFirstByJobConfigAndStatus(LogReviewJobConfig jobConfig, LogReviewStatusType status)
  List<ExecutedLogReview> findByJobConfig(LogReviewJobConfig jobConfig)
  List<ExecutedLogReview> findByStatus(LogReviewStatusType status)
  List<ExecutedLogReview> findByJobConfigAndStatus(LogReviewJobConfig jobConfig, LogReviewStatusType status)
}
