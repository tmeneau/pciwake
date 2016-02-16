import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder

db {
  jdbcString = "jdbc:mysql://192.168.59.104:3306/pciwake"
  username = "sa"
  password = "sa"
}

auth { 
  authClosure = { AuthenticationManagerBuilder authBuilder ->
    authBuilder.ldapAuthentication()
      .userDnPatterns("uid={0},cn=users,cn=accounts")
      .groupSearchBase("cn=groups,cn=accounts")
      .contextSource()
        .url("ldaps://ldap.domain.com:636/dc=domain,dc=com")
  }
  
  adminUser = "admin"
  adminPass = "pass"
}

notificationRetryJobFrequency = new org.quartz.CronExpression("0/10 * * * * ?")