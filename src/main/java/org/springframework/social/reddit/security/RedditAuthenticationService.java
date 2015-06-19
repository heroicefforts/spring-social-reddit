package org.springframework.social.reddit.security;

import org.springframework.social.reddit.api.Reddit;
import org.springframework.social.reddit.connect.RedditConnectionFactory;
import org.springframework.social.security.provider.OAuth2AuthenticationService;

public class RedditAuthenticationService extends OAuth2AuthenticationService<Reddit> {

	public RedditAuthenticationService(String apiKey, String appSecret, String userAgent) {
		super(new RedditConnectionFactory(apiKey, appSecret, userAgent));
	}

}
