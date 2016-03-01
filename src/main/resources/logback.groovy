import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import static ch.qos.logback.classic.Level.*


appender("CONSOLE", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{req.xForwardedFor}] %-5level %logger{0} - %msg%n"
  }
}

if (System.getProperty("DISABLE_LOGGING")) {
  root(OFF, ["CONSOLE"])
} else {
  
  /*
   * CXF logs requests to un-serviced endpoints and client disconnects as "WARN",
   * which can make things a little verbose
   */
  logger("org.apache.cxf.jaxrs.interceptor.JAXRSInInterceptor", ERROR)
  logger("org.apache.cxf.phase.PhaseInterceptorChain", ERROR)
  logger("org.apache.cxf.jaxrs.impl.WebApplicationExceptionMapper", ERROR)
  
  root(INFO, ["CONSOLE"])
}