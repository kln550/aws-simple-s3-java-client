package com.s3client;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import  com.google.gson.Gson;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

class OauthToken {
  String access_token;
  String refresh_token;
  String id;
}

public class RestClientUtil {
  private Log log = LogFactory.getLog(RestClientUtil.class);

  /* Gets the Oauth token from auth service */
  public String getAccessToken(String clientId, String clientSecret, String loginUrl) {
   log.info("*** Start getAccessToken ***");
    try (CloseableHttpClient httpclient = createAcceptSelfSignedCertificateClient()) {

        // Create new getRequest with below mentioned URL
        HttpPost postRequest = new HttpPost(loginUrl);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("client_id", clientId));
        params.add(new BasicNameValuePair("client_secret", clientSecret));
        postRequest.setEntity(new UrlEncodedFormEntity(params));

        postRequest.getEntity().writeTo(System.out);

        // Execute your request and catch response
        HttpResponse response = httpclient.execute(postRequest);

        // Check for HTTP response code: 200 = success
        if (response.getStatusLine().getStatusCode() != 200) {
          throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
        }

        BasicResponseHandler  handler = new BasicResponseHandler();
        String responseBody = handler.handleResponse(response);
        log.info("\nresponseBody: "+responseBody);
        System.out.println("\nresponseBody: "+responseBody);

        OauthToken oauthToken = new Gson().fromJson(responseBody, OauthToken.class);
        return oauthToken.access_token;

    } catch (Exception ex) {
       log.error("*** Error in getAccessToken *** "+ex.getMessage());
       ex.printStackTrace();
       throw new RuntimeException(ex.toString());
    }

  }

  private static CloseableHttpClient createAcceptSelfSignedCertificateClient()
          throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

    // use the TrustSelfSignedStrategy to allow Self Signed Certificates
    SSLContext sslContext = SSLContextBuilder
            .create()
            .loadTrustMaterial(new TrustSelfSignedStrategy())
            .build();

    // we can optionally disable hostname verification.
    // if you don't want to further weaken the security, you don't have to include this.
    HostnameVerifier allowAllHosts = new NoopHostnameVerifier();

    // create an SSL Socket Factory to use the SSLContext with the trust self signed certificate strategy
    // and allow all hosts verifier.
    SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);

    // finally create the HttpClient using HttpClient factory methods and assign the ssl socket factory
    return HttpClients
            .custom()
            .setSSLSocketFactory(connectionFactory)
            .build();
  }


  /* Gets the Oauth token from auth service */
  public String getUrl(String url) {
    try (CloseableHttpClient httpclient = createAcceptSelfSignedCertificateClient()) {

      // Create new getRequest with below mentioned URL
      HttpGet getRequest = new HttpGet(url);

      // Execute your request and catch response
      HttpResponse response = httpclient.execute(getRequest);

      // Check for HTTP response code: 200 = success
      if (response.getStatusLine().getStatusCode() != 200) {
        throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
      }

      BasicResponseHandler  handler = new BasicResponseHandler();
      String responseBody = handler.handleResponse(response);
      System.out.println(" responseBody: "+responseBody);
      return responseBody;
    } catch (Exception ex){
      ex.printStackTrace();
      throw new RuntimeException(ex.toString());
    }

  }
}


