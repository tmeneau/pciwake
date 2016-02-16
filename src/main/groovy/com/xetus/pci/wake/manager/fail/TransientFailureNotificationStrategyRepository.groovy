package com.xetus.pci.wake.manager.fail

import org.springframework.data.jpa.repository.JpaRepository;

interface TransientFailureNotificationStrategyRepository 
          extends JpaRepository<TransientFailureNotificationStrategy, Long> {}
