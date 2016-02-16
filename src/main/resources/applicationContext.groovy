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
  
  
  def useH2 = environment.systemProperties["pciwake.useEmbeddedDb"] == "true"
    
  /*
   * Handle using an external or embedded database. Note that the embedded
   * database is an h2 database while the external database must currently
   * be a mysql database 
   */
  if (useH2) {
    dataSource(org.springframework.jdbc.datasource.DriverManagerDataSource) { bean ->
      url = "jdbc:h2:./build/embedded/h2/db:pciwake"
      driverClassName = "org.h2.Driver"
      username = ""
      password = ""
    }
  } else {
    dataSource(org.springframework.jdbc.datasource.DriverManagerDataSource) { bean ->
      url = "#{siteConfig.dbConfig.jdbcString}"
      driverClassName = "com.mysql.jdbc.Driver"
      username = "#{siteConfig.dbConfig.username}"
      password = "#{siteConfig.dbConfig.password}"
    } 
  }
  
  entityManagerFactory(org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean){
    packagesToScan = "com.xetus.pci.wake"
    jpaVendorAdapter = new org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter()
    dataSource = ref("dataSource")
    
    def props = [
      "hibernate.hbm2ddl.auto": "update",
    ]
    
    if (useH2) {
      props << [
        "hibernate.connection.driver_class": "org.h2.Driver",
        "hibernate.dialect": "org.hibernate.dialect.H2Dialect"
      ]
    } else {
      props << [
        "hibernate.connection.driver_class": "com.mysql.jdbc.Driver",
        "hibernate.dialect": "org.hibernate.dialect.MySQLDialect"
      ]
    }
    
    jpaProperties = props
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
    ignoredUnknownProperties = "applicationContext"
  }
  
  // ensure the Quartz tables are created on startup
  def quartzInitScript = [new ClassPathResource("quartz_tables_h2.sql")]
  
  if (!useH2) {
    quartzInitScript = new ClassPathResource('quartz_tables_mysql.sql')
    
    /*
     * this is required due to the Quartz SQL initializer script not being
     * idempotent
     */
    mysqlCreateIndexIfNotExistsProcedure(org.springframework.jdbc.datasource.init.DataSourceInitializer) { bean ->
      dataSource = ref('dataSource')
      enabled = true
      databasePopulator = new ResourceDatabasePopulator(
        scripts: new ClassPathResource('mysql_create_index_if_not_exists.sql'),
        continueOnError: false,
        ignoreFailedDrops: true,
        sqlScriptEncoding: "UTF-8",
        separator: "\$\$"
      )
    }
  }
  
  quartzDbInit(org.springframework.jdbc.datasource.init.DataSourceInitializer) { bean ->
    if (!useH2) {
      bean.dependsOn = "mysqlCreateIndexIfNotExistsProcedure"
    }
    dataSource = ref('dataSource')
    enabled = true
    databasePopulator = new ResourceDatabasePopulator(
      scripts: quartzInitScript,
      continueOnError: false,
      ignoreFailedDrops: true,
      sqlScriptEncoding: "UTF-8"
    )
  }
  
  schedulerFactory(org.springframework.scheduling.quartz.SchedulerFactoryBean) { bean ->
    bean.dependsOn = "quartzDbInit"
    dataSource = ref('dataSource')
    transactionManager = ref('transactionManager')
    jobFactory = ref('quartzJobFactory')
    applicationContextSchedulerContextKey = "applicationContext"
    schedulerContextAsMap = [:]
    waitForJobsToCompleteOnShutdown = true
    configLocation = new ClassPathResource("quartz.properties")
    autoStartup = true
  }
    
}