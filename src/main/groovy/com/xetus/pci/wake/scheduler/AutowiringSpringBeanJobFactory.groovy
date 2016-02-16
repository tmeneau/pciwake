package com.xetus.pci.wake.scheduler

import groovy.transform.CompileStatic

import org.quartz.spi.TriggerFiredBundle

import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.scheduling.quartz.SpringBeanJobFactory

/**
 * A spring job bean job factory that instantiates Quarts jobs to be 
 * Spring aware (allowing for autowired/injected Spring bean references
 * from within the job)
 */
@CompileStatic
public class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory 
                                            implements ApplicationContextAware {
  private transient AutowireCapableBeanFactory beanFactory

  void setApplicationContext(final ApplicationContext context) {
    beanFactory = context.getAutowireCapableBeanFactory()
  }

  @Override
  Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
    final Object job = super.createJobInstance(bundle)
    beanFactory.autowireBean(job)
    return job
  }
}