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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONValue;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.social.reddit.api.impl.RedditPaths;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author ahmedaly
 */
public class RedditOAuth2Template extends OAuth2Template {
	private final static Log logger = LogFactory.getLog(RedditOAuth2Template.class);

	private CloseableHttpClient client;
	
	
    public RedditOAuth2Template(CloseableHttpClient client, String clientId, String clientSecret) {
        super(clientId, clientSecret, RedditPaths.OAUTH_AUTH_URL, RedditPaths.OAUTH_TOKEN_URL);
        this.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
        this.client = client;
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
    	MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
    	params.add("code", code);
    	params.add("grant_type", "authorization_code");
    	params.add("redirect_uri", redirectUrl);
    	
    	long start = System.currentTimeMillis();
    	Map<?, ?> json = this.getRestTemplate().postForObject(RedditPaths.OAUTH_TOKEN_URL, params, Map.class);
        logger.debug("Reddit response time:  " + (System.currentTimeMillis() - start) + " ms.");
        if (json != null && json.containsKey("access_token")) {
            return (String) (json.get("access_token"));
        }
    	
        return null;
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
