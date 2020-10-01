package com.s3client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class AwsS3ClientTest {
  private Log log = LogFactory.getLog(AwsS3ClientTest.class);
  private String clientId;
  private String clientSecret;
  private String loginURL;
  private String awsTokenUrl;

  @Before
  public void getTestParams() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
      clientId = System.getProperty("client_id");
      clientSecret = System.getProperty("client_secret");
      loginURL = System.getProperty("loginUrl");
      awsTokenUrl = System.getProperty("awsTokenUrl");
      log.info("clientId: " + clientId);
      log.info("clientSecret: " + clientSecret);
      log.info("loginURL: " + loginURL);
      log.info("awsTokenUrl: " + awsTokenUrl);
  }

  @Test
  public void testVerifyGetAwsTokens() throws IOException {
      RestClientUtil restClientUtil = new RestClientUtil();
      String accessToken = restClientUtil.getAccessToken(clientId, clientSecret, loginURL);
      assertNotNull(accessToken);
      assertNotSame("", accessToken);

      AwsTokenDetails awsTokenDetails = restClientUtil.getAwsAccessKeys(accessToken, awsTokenUrl);
      assertNotNull(awsTokenDetails);
      assertNotNull(awsTokenDetails.credentials);
      assertNotNull(awsTokenDetails.credentials.acccessKeyId);
      assertNotNull(awsTokenDetails.credentials.secreteAccessKey);
      assertNotNull(awsTokenDetails.credentials.sessionToken);
  }

/*    @Test
    public void testHttpClient() throws Exception {
        RestClientUtil restClientUtil = new RestClientUtil();
        restClientUtil.getUrl("https://www.examples.com/");
        restClientUtil.getUrl("https://www.verisign.com/");
    }
 */

}
