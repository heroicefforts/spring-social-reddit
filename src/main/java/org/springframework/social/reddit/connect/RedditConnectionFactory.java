package org.springframework.social.reddit.connect;

import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.reddit.api.Reddit;

/**
 *
 * @author ahmedaly
 */
public class RedditConnectionFactory extends OAuth2ConnectionFactory<Reddit> {

    public RedditConnectionFactory(String clientId, String clientSecret, String userAgent) {
        super("reddit", new RedditServiceProvider(clientId, clientSecret, userAgent), new RedditAdapter());
    }

    @Override
    protected String extractProviderUserId(AccessGrant accessGrant) {
        Reddit api = ((RedditServiceProvider) getServiceProvider()).getApi(accessGrant.getAccessToken());
        UserProfile userProfile = getApiAdapter().fetchUserProfile(api);
        return userProfile.getUsername();
    }

}
