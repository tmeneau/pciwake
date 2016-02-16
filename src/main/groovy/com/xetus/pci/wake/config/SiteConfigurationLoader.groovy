package com.xetus.pci.wake.config

import org.codehaus.groovy.control.CompilerConfiguration

import groovy.transform.CompileStatic

import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class SiteConfigurationLoader {
  
  static SiteConfiguration buildConfig(){
    def cc = new CompilerConfiguration()
    def cl = SiteConfigurationLoader.class.classLoader

    cc.setScriptBaseClass(DelegatingScript.class.name);
    def shell = new GroovyShell(cl, new Binding(), cc)
    
    DelegatingScript script = (DelegatingScript) shell.parse(
       SiteConfigurationLoader.class.getResource("/defaultconfig.groovy").toURI())
    
    def config = new SiteConfiguration()
    script.setDelegate(config)
    script.run()
    
    File externalConfigDir = new File("/etc/pciwake/")
    if (System.getProperty("pciwake.config")){
      externalConfigDir = new File(
          System.getProperty("pciwake.config"))
    } else if (System.getenv()["PCIWAKE_CONFIG"]){
      externalConfigDir = new File(System.getenv()["PCIWAKE_CONFIG"])
    }
    config.externalConfigDir = externalConfigDir
    File overrideFile = new File(externalConfigDir, "siteconfig.groovy")

    if (overrideFile.exists()){
      script = (DelegatingScript) shell.parse(overrideFile.text)
      script.setDelegate(config)
      script.run()
    }
    
    log.info "Resolved config: $config"
    return config
  }
}
