/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.springframework.social.reddit.api.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.reddit.api.MessageOperations;
import org.springframework.social.reddit.api.Reddit;
import org.springframework.social.reddit.api.SubredditOperations;
import org.springframework.social.reddit.api.ThreadOperations;
import org.springframework.social.reddit.api.UserOperations;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of Reddit API Binding interface.
 *
 * <p>
 * The Reddit REST Api requires authentication via OAuth2 to access most
 * endpoint resources. Upon authentication, this is the implementation for
 * accessing the RESTful Reddit API.
 * </p>
 *
 * @author ahmedaly
 */
public class RedditTemplate extends AbstractOAuth2ApiBinding implements Reddit {

    private String accessToken;

    private MessageOperations messageOperations;
    private UserOperations userOperations;
    private SubredditOperations subredditOperations;
    private ThreadOperations threadOperations;

    public RedditTemplate() {
        setUp("a user agent");
    }

    public RedditTemplate(String userAgent, String accessToken) {
        super(accessToken);
        this.accessToken = accessToken;
        setUp(userAgent);
    }

    private final class UserAgentInterceptor implements ClientHttpRequestInterceptor {
    	private final String userAgent;

		public UserAgentInterceptor(String userAgent) {
    		this.userAgent = userAgent;
    	}
    	
		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
				throws IOException {
			HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
			requestWrapper.getHeaders().set("User-Agent", userAgent);
			 
			return execution.execute(requestWrapper, body);
		}
    }
    
    private void setUp(final String userAgent) {
    	RestTemplate template = getRestTemplate();

    	ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
    	if(template.getInterceptors() != null)
    		interceptors.addAll(template.getInterceptors());
    	interceptors.add(new UserAgentInterceptor(userAgent));
    	template.setInterceptors(interceptors);
    	
        this.userOperations = new UserTemplate(template, isAuthorized());
    }

    @Override
    public MessageOperations messageOperations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SubredditOperations subredditOperations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ThreadOperations threadOperations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public UserOperations userOperations() {
        return userOperations;
    }

}
