package com.xetus.pci.wake.notification

import groovy.text.SimpleTemplateEngine
import groovy.transform.CompileStatic

import org.springframework.core.GenericTypeResolver


/**
 * An abstract {@link NotificationProcessor} class that implements the
 * {@link Plugin#supports(Object)} method by pulling the supported {@link
 * Notification} implementation type from the generic type parameter.
 *
 * While not necessary, subclassing this instead of implementing
 * {@link NotificationProcessor} is recommended.
 *
 * @param <N> the {@link Notification} implementation supported by the
 * {@link NotificationProcessor} implementation
 */
@CompileStatic
abstract class AbstractNotificationProcessorPlugin<N extends Notification>
         implements NotificationProcessorPlugin<N> {
  
  @Override
  boolean supports(Notification notification) {
    Class<N> clazz = GenericTypeResolver.resolveTypeArgument(
      getClass(),
      AbstractNotificationProcessorPlugin.class
    )
    return clazz.equals(notification.getClass())
  }
  
  /**
   * Resolves the variables in the supplied template using an instance of the 
   * {@link SimpleTemplateEngine}. 
   * 
   * @param template the template whose variables to resolve. 
   * @param dataBinding the data binding to use to resolve the template's
   * variables.
   * 
   * @return the String with all variables resolved against the supplied data
   * binding.
   */
  String resolveTemplate(String template, Map<String, Object> dataBinding) {
    SimpleTemplateEngine engine = new SimpleTemplateEngine()
    return engine.createTemplate(template).make(dataBinding)
  }
}
