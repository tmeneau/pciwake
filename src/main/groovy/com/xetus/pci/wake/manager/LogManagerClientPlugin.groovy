package com.xetus.pci.wake.manager

import groovy.transform.CompileStatic

import org.springframework.plugin.core.Plugin

/**
 * The client responsible or actually issuing query requests to log manager
 * instances based on the log-manager-type-specific {@link LogManagerConfig}
 * implementation's configuration.
 * 
 * client implementations must define which {@link LogManagerConfig} instances
 * they can support through implementation of the {@link 
 * LogManagerClientPlugin#supports(Object)} method.
 * 
 * @param <T> The specific {@link LogManagerConfig} implementation supported
 * by the {@link LogManagerClientPlugin} implementation.
 */
@CompileStatic
interface LogManagerClientPlugin<T extends LogManagerClientConfig> 
          extends Plugin<T> {
  
  /**
   * Issues the request to the log manager at the supplied endpoint using the 
   * connection information in the supplied config, returning the unparsed,
   * unprocessed content of the log manager's response.
   * 
   * This is intended for internal subclass and testing usage only.
   * 
   * @param config
   * @param endpoint
   * 
   * @return the unparsed, unprocessed response content from the log manager
   * instance.
   */
  String getResultText(T config, URI endpoint)
  
  /**
   * Issues the request to the log manager using the supplied config, start
   * time and end time and returns the generated {@link LogQueryResult}
   * 
   * @param config
   * @param startTime
   * @param endTime
   * 
   * @return the {@link LogQueryResult} instance generated from the log manager's
   * response to the issued query
   */
  LogQueryResult getLogs(T config, Date startTime, Date endTime)
}
