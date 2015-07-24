/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.springframework.social.reddit.connect;

import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.reddit.api.Reddit;
import org.springframework.social.reddit.api.impl.RedditTemplate;

/**
 *
 * @author ahmedaly
 */
public final class RedditServiceProvider extends AbstractOAuth2ServiceProvider<Reddit> {
	private CloseableHttpClient client;
	
    public RedditServiceProvider(CloseableHttpClient httpClient, String clientId, String clientSecret) {
        super(new RedditOAuth2Template(httpClient, clientId, clientSecret));
        this.client = httpClient;
    }

    @Override
    public Reddit getApi(String accessToken) {
        return new RedditTemplate(client, accessToken);
    }

}
