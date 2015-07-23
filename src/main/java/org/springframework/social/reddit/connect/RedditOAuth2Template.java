/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.springframework.social.reddit.connect;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONValue;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.social.reddit.api.impl.RedditPaths;
import org.springframework.social.reddit.config.support.RedditApiHelper;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;

/**
 *
 * @author ahmedaly
 */
public class RedditOAuth2Template extends OAuth2Template {
	private final static Log logger = LogFactory.getLog(RedditOAuth2Template.class);

	private String userAgent;
	private CloseableHttpClient client;
	
	
    public RedditOAuth2Template(String clientId, String clientSecret, String userAgent) {
        super(clientId, clientSecret, RedditPaths.OAUTH_AUTH_URL, RedditPaths.OAUTH_TOKEN_URL);
        this.userAgent = userAgent;
        
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(50);
        cm.setDefaultMaxPerRoute(50);
        
        CredentialsProvider credp = new BasicCredentialsProvider();
        credp.setCredentials(new AuthScope("ssl.reddit.com", 443), new UsernamePasswordCredentials(clientId,clientSecret));
        
        client = HttpClientBuilder.create()
        	.setConnectionManager(cm)
        	.setDefaultCredentialsProvider(credp)
        	.setRetryHandler(new RedditRetryHandler()) //Reddit QoS is really shitty
       		.build();
    }

    private class RedditRetryHandler implements HttpRequestRetryHandler {

        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        	logger.debug("Encountered Reddit http error.", exception);
        	
            if (executionCount < 2) {
	            if (exception.getCause() != null && exception.getCause() instanceof HttpServerErrorException) {
	            	logger.warn("Recovering from Reddit server error:  " + exception.getCause().getMessage());
	                return true;
	            }
            }
            
            return false;
        }

    }    
    
    public String buildAuthenticateUrl(OAuth2Parameters parameters) {
    	parameters.setScope("identity");
    	return super.buildAuthenticateUrl(parameters);
    }
    
    public String buildAuthenticateUrl(GrantType grantType, OAuth2Parameters parameters) {
    	parameters.setScope("identity");
    	return super.buildAuthenticateUrl(grantType, parameters);
    }
    
    private String getAccessToken(String code, String redirectUrl) throws UnsupportedEncodingException, IOException {
    	long start = System.currentTimeMillis();
    			
        try {
            //Reddit Requires clientId and clientSecret be attached via basic auth

            HttpPost httppost = new HttpPost(RedditPaths.OAUTH_TOKEN_URL);
            
            List<NameValuePair> nvps = new ArrayList<NameValuePair>(3);
            nvps.add(new BasicNameValuePair("code", code));
            nvps.add(new BasicNameValuePair("grant_type", "authorization_code"));
            nvps.add(new BasicNameValuePair("redirect_uri", redirectUrl));

            httppost.setEntity(new UrlEncodedFormEntity(nvps));
            httppost.addHeader("User-Agent", userAgent);
            httppost.setHeader("Accept", "any;");

            CloseableHttpResponse response = client.execute(httppost); 
            HttpEntity entity = response.getEntity(); // Reddit response containing accessToken
            
            if (entity != null) {
            	InputStream is = null;
            	try {
	                BufferedReader br = new BufferedReader(new InputStreamReader(is = entity.getContent()));
	                StringBuilder content = new StringBuilder();
	                String line;
	                while (null != (line = br.readLine())) {
	                    content.append(line);
	                }
	                System.out.println(content.toString());
	                Map json = (Map) JSONValue.parse(content.toString());
	                br.close();
	                if (json != null && json.containsKey("access_token")) {
	                    return (String) (json.get("access_token"));
	                }
            	}
            	finally {
            		closeQuietly(is);
            		response.close();
            	}
            }
            EntityUtils.consume(entity);
        } finally {
            logger.debug("Reddit response time:  " + (System.currentTimeMillis() - start) + " ms.");
        }
        return null;
    }
    
    private static final void closeQuietly(Closeable c) {
    	try {
    		c.close();
    	}
    	catch(Exception e) {}
    }
    
    @Override
    public AccessGrant exchangeForAccess(String authorizationCode, String redirectUri, MultiValueMap<String, String> additionalParameters) {
        try {
            String accessToken = getAccessToken(authorizationCode, redirectUri);
            AccessGrant grant = new AccessGrant(accessToken);
            return grant;
        } catch (IOException ex) {
            logger.error("Error during exchange.", ex);
        }
        return null;
    }
    
}
