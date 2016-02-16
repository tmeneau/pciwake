import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator

beans { 
  
  xmlns([
    ctx:'http://www.springframework.org/schema/context',
    jpa:'http://www.springframework.org/schema/data/jpa',
    jdbc:"http://www.springframework.org/schema/jdbc",
    tx:'http://www.springframework.org/schema/tx'
  ])
  
  
  /*
   * Spring IOC configuration
   */
  ctx.'component-scan'('base-package': "com.xetus.pci.wake")
  ctx.'annotation-config'(true)
  
  /*
   * JPA configuration
   */
  jpa.'repositories'('base-package': 'com.xetus.pci.wake')
  
  siteConfig(org.springframework.beans.factory.config.MethodInvokingFactoryBean){
    targetClass = "com.xetus.pci.wake.config.SiteConfigurationLoader"
    targetMethod = "buildConfig"
  }
  
  dataSource(org.springframework.jdbc.datasource.DriverManagerDataSource) { bean ->
    url = "jdbc:h2:mem:pciwaketest"
    driverClassName = "org.h2.Driver"
    username = ""
    password = ""
  }
  
  entityManagerFactory(org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean){
    packagesToScan = "com.xetus.pci.wake"
    jpaVendorAdapter = new org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter()
    dataSource = ref("dataSource")
    
    def jpaProperties = [
      "hibernate.hbm2ddl.auto": "create",
      "hibernate.connection.driver_class": "org.h2.Driver",
      "hibernate.dialect": "org.hibernate.dialect.H2Dialect"
    ]
  }
  
  transactionManager(org.springframework.orm.jpa.JpaTransactionManager){
    entityManagerFactory = ref("entityManagerFactory")
  }

  tx.'annotation-driven'('transaction-manager': 'annotation-driven')
  
  /*
   * Spring-Quartz (scheduler) configuration
   */
  
  // support Spring injection into job instances
  quartzJobFactory(com.xetus.pci.wake.scheduler.AutowiringSpringBeanJobFactory) { bean ->
    ignoredUnknownProperties = "testApplicationContext"
  }
  
  /*
  // ensure the Quartz tables are created on startup
  def quartzInitScript = [new ClassPathResource("quartz_tables_h2.sql")]
  
  quartzDbInit(org.springframework.jdbc.datasource.init.DataSourceInitializer) { bean ->
    dataSource = ref('dataSource')
    enabled = true
    databasePopulator = new ResourceDatabasePopulator(
      scripts: quartzInitScript,
      continueOnError: false,
      ignoreFailedDrops: true,
      sqlScriptEncoding: "UTF-8"
    )
  }
  */
  
  /*
   * override bootstrap, since we don't want any up-front interactions with
   * the scheduler engine if we can avoid it.
   */
  bootstrap(com.xetus.pci.wake.test.TestBootstrap) { bean -> }
  
  schedulerFactory(org.mockito.Mockito, org.springframework.scheduling.quartz.SchedulerFactoryBean) { bean ->
    bean.factoryMethod = "mock"
  }
  
  schedulerEngine(com.xetus.pci.wake.scheduler.SchedulerEngine) { bean ->
    bean.dependsOn = "schedulerFactory"
  }
    
}