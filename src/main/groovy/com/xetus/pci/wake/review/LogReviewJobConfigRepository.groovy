package com.xetus.pci.wake.review

import groovy.transform.CompileStatic

import org.springframework.data.jpa.repository.JpaRepository

@CompileStatic
interface LogReviewJobConfigRepository 
          extends JpaRepository<LogReviewJobConfig, Long> {
  LogReviewJobConfig findByJobNameAndJobGroup(String jobName, String jobGroup)
}
