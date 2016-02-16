package com.xetus.pci.wake.manager

import java.net.ConnectException;

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import org.apache.http.HttpHeaders
import org.apache.http.HttpResponse
import org.apache.http.NoHttpResponseException
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.client.methods.HttpGet
import org.apache.http.config.SocketConfig
import org.apache.http.conn.ConnectTimeoutException
import org.apache.http.conn.HttpClientConnectionManager
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.message.BasicHeader

import org.springframework.core.GenericTypeResolver

import com.xetus.pci.wake.manager.fail.TransientLogManagerQueryException

/**
 * An bastract {@link LogManagerClientPlugin} class that implements the 
 * {@link Plugin#supports(Object)} method by pulling the supported {@link 
 * LogManagerConfig} implementation type from the generic type parameter.
 * 
 * In addition, this implements some of the (high) low level work for actually
 * building and issuing the HTTP request to the log manager instance using an
 * {@link HttpClient}, allowing subclasses to focus on generating the log
 * manager specific endpoint that will return the query result.
 * 
 * While not necessary, subclassing this instead of implementing {@link 
 * LogManagerClientPlugin} is recommended.
 * 
 * @param <T> the {@link LogManagerConfig} implementation supported by
 * this {@link LogManagerClientPlugin} implementation.
 */
@Slf4j
@CompileStatic
abstract class AbstractLogManagerClientPlugin<T extends LogManagerClientConfig> 
          implements LogManagerClientPlugin<T> {

  HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager()
  
  @Override
  boolean supports(LogManagerClientConfig config) {
    Class<T> clazz = GenericTypeResolver.resolveTypeArgument(
      getClass(),
      AbstractLogManagerClientPlugin.class
    )
    return clazz.equals(config.getClass())
  }
  
  /**
   * Separates the logic for generating the HTTP client from the logic 
   * for retrieving the result text. This is mostly for testing purposes.
   * 
   * @param config
   * @param endpoint
   * @return
   */
  public CloseableHttpClient getHttpClient(T config, URI endpoint) {
    HttpClientBuilder builder = HttpClientBuilder
      .create()
      .useSystemProperties()
      .setConnectionManager(cm)
      .setConnectionManagerShared(true)
      .setDefaultHeaders([new BasicHeader(HttpHeaders.ACCEPT, "application/json")])
      .setDefaultSocketConfig(SocketConfig.custom()
        .setSoTimeout(30*1000)
        .setTcpNoDelay(true)
        .build())
    
    if (config.connectionConfig?.authContext != null) {
      AuthenticationContext authContext = config.connectionConfig.authContext
      CredentialsProvider credsProvider = new BasicCredentialsProvider();
      credsProvider.setCredentials(
        new AuthScope(endpoint.getHost(), endpoint.getPort()),
        new UsernamePasswordCredentials(
          authContext.username, 
          authContext.password)
      );
      builder.setDefaultCredentialsProvider(credsProvider)
    }
    
    return builder.build()
  }
  
  /**
   * Uses the configurations in the supplied {@link 
   * LogManagerConfig#connectionConfig} to generate and issue the HTTP request
   * to the supplied endpoint. Any post-processing of the returned content is
   * left up to the {@link AbstractLogManagerClientPlugin} implementation.  
   * 
   * @param config
   * @param endpoint
   * 
   * @return the unparsed text of the result content
   */
  @Override
  public String getResultText(T config, URI endpoint) {
    HttpGet get = new HttpGet(endpoint)
    
    CloseableHttpClient client = getHttpClient(config, endpoint)
    String result
    
    try {
      HttpResponse response = client.execute(get)
      InputStream is = response.getEntity().getContent()
      result = is.text
      log.debug "response: $result"
    } catch(NoHttpResponseException e) {
      throw new TransientLogManagerQueryException("encountered transient "
        + "exception attempting to query log manager instance", e)
    } catch(ConnectTimeoutException | ConnectException | SocketException e) {
      throw new TransientLogManagerQueryException("encountered transient "
        + "exception attempting to query log manager instance; please "
        + "verify your connection configuration for the log manager instance "
        + "is correct", e)
    } catch(e) {
      log.error("encountered unhandled exception attempting to issue HTTP "
        + "request to log manager instance", e)
      throw e
    } finally {
      client.close()
    }
    
    return result
  }
}
