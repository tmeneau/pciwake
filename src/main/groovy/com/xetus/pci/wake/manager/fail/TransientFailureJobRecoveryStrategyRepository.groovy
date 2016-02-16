package com.xetus.pci.wake.manager.fail

import org.springframework.data.jpa.repository.JpaRepository;

interface TransientFailureJobRecoveryStrategyRepository
          extends JpaRepository<TransientFailureJobRecoveryStrategy, Long> {}
