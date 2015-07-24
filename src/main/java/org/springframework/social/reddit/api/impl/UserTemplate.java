package org.springframework.social.reddit.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.social.reddit.api.RedditProfile;
import org.springframework.social.reddit.api.UserOperations;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author ahmedaly
 */
public class UserTemplate extends AbstractRedditOperations implements UserOperations {
	private final Log logger = LogFactory.getLog(UserTemplate.class);
    
    private static final String USER_PROFILE = RedditPaths.OAUTH_API_DOMAIN + "/api/v1/me.json";
    private static final String USER_KARMA = RedditPaths.OAUTH_API_DOMAIN + "/api/v1/me/karma"; //Requires mysubreddits scope
    private static final String USER_PREFERENCES = RedditPaths.OAUTH_API_DOMAIN + "/api/v1/me/prefs"; //Requires Account Scope
    
    public UserTemplate(RestTemplate restTemplate, boolean isAuthorized){
        super(restTemplate, isAuthorized);
    }
    
    @Override
    public RedditProfile getUserProfile() {
    	logger.debug("Fetching Reddit user profile...");
    	long start = System.currentTimeMillis();
        RedditProfile profile = getEntity(USER_PROFILE, RedditProfile.class);
    	logger.debug("Fetched Reddit user profile in " + (System.currentTimeMillis() - start) + " ms.");
        return profile;
    }
    
    
    
}
