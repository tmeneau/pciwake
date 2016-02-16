package com.xetus.pci.wake.review.incident

import groovy.transform.CompileStatic

import org.springframework.data.jpa.repository.JpaRepository

@CompileStatic
interface LogIncidentRepository extends JpaRepository<LogIncident, Long> {}