package com.xetus.pci.wake.notification

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import java.util.concurrent.CopyOnWriteArrayList

import javax.inject.Inject

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.plugin.core.PluginRegistry
import org.springframework.stereotype.Component

/**
 * A service for sending notifications through any number of mediums and with
 * multiple configurations simultaneously.
 */
@Slf4j
@Component
@CompileStatic
class NotificationService {
  
  /*
   * TODO: Eclipse incorrectly throws an error here due to the parameterization
   * of NotificationProcessorPlugin (it expects PluginRegistry to have a 
   * signature of: `PluginRegistry<T extends Plugin<S>, S>`, but here we're
   * using: `PluginRegistry<T<N extends S> extends Plugin<S>, S>`; this is a 
   * false positive and can be ignored.
   */
  @Inject
  @Qualifier("notificationProcessorPluginRegistry")
  PluginRegistry<NotificationProcessorPlugin<? extends Notification>, ? extends Notification> notificationProcessorRegistry
  
  /**
   * A very simple queue of notification requests that failed for transient
   * reasons. Note that this is for in-memory purposes only and does not 
   * currently support persistence (if the JVM dies, you lose your queue).
   */
  protected List<QueuedNotificationRequest> queuedNotifications = new CopyOnWriteArrayList<QueuedNotificationRequest>()
  
  public void retryQueued(QueuedNotificationRequest queued) {
    queuedNotifications.remove(queued)
    notify(queued.notifications, queued.binding)
  }
  
  /**
   * Issues each of the supplied notifications using the applicable plugin for
   * the particular plugin. This means that each notification can potentially
   * notify through more than one medium (e.g. through email and jabber).
   * 
   * @param notifications
   * @param binding
   * 
   * @throws IllegalArgumentException if no {@link NotificationProcessorPlugin}
   * can be found that can process the supplied {@link Notification} type. 
   * Determination of type support is determined via the {@link 
   * NotificationProcessorPlugin#supports(Object)} method.
   */
  public void notify(List<Notification> notifications, 
                     Map<String, Object> binding) {
    
    QueuedNotificationRequest queued = new QueuedNotificationRequest(
      binding: binding
    )
    
    notifications.each { Notification notification ->
      if (notification != null) {
        
        NotificationProcessorPlugin processor = notificationProcessorRegistry
          .getPluginFor(notification)
          
        if (processor == null) {
          throw new IllegalArgumentException("Failed to locate "
            + "NotificationProcessor plugin applicable for type: "
            + notification.class.name +"; available plugins: "
            + (notificationProcessorRegistry.plugins?.collect { it.class.name }))
        }
        
        try {
          processor.process(notification, binding)
          
        } catch(TransientNotificationException e) {
          log.warn "Failed to send notification; queueing", e
          queued.add(notification)
          
        } catch(Exception e) {
          log.error("Failed to send notification, and error wasn't transient; "
                  + "giving up", e)
        }
        
      }
    }
    
    if (!queued.empty()) {
      queuedNotifications << queued
    }
  }
  
}
