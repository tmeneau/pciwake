package com.xetus.pci.wake.notification

import groovy.transform.CompileStatic

import org.springframework.plugin.core.Plugin

/**
 * The processor responsible for actually issuing notifications based on a 
 * medium-specific {@link Notification} implementation and with variable
 * substitution of notification content using the {@link SimpleTemplateEngine}.
 *  
 * Processor implementations must define which {@link Notification} instances
 * they can support through implementation of the {@link 
 * NotificationProcessorPlugin#supports(Object)} method.
 * 
 * @param <N> The specific {@link Notification} implementation supported by
 * the {@link NotificationProcessorPlugin} implementation.
 */
@CompileStatic
interface NotificationProcessorPlugin<N extends Notification> 
          extends Plugin<N> {

  /**
   * Process and deliver the supplied {@link Notification} instance, using the 
   * supplied <code>dataBinding</code> to perform template substitution on the
   * notification's content.
   *
   * @param notification
   * @param dataBinding
   */
  public void process(N notification, Map<String, Object> dataBinding) 
              throws TransientNotificationException
}
