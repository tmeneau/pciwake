package com.xetus.pci.wake

import groovy.transform.CompileStatic

import org.springframework.context.annotation.Configuration
import org.springframework.plugin.core.config.EnablePluginRegistries

import com.xetus.pci.wake.manager.LogManagerClientPlugin
import com.xetus.pci.wake.notification.NotificationProcessorPlugin

@CompileStatic
@Configuration
@EnablePluginRegistries([
  NotificationProcessorPlugin.class,
  LogManagerClientPlugin.class
])
class ApplicationConfiguration {}
