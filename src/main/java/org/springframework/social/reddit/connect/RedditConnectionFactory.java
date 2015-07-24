package org.springframework.social.reddit.connect;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.reddit.api.Reddit;
import org.springframework.web.client.HttpServerErrorException;

/**
 *
 * @author ahmedaly
 */
public class RedditConnectionFactory extends OAuth2ConnectionFactory<Reddit> {
	private final static Log logger = LogFactory.getLog(RedditConnectionFactory.class);

    public RedditConnectionFactory(String clientId, String clientSecret, String userAgent) {
        super("reddit", new RedditServiceProvider(httpClient(clientId, clientSecret, userAgent), clientId, clientSecret), 
        		new RedditAdapter());
    }

    @Override
    protected String extractProviderUserId(AccessGrant accessGrant) {
        Reddit api = ((RedditServiceProvider) getServiceProvider()).getApi(accessGrant.getAccessToken());
        UserProfile userProfile = getApiAdapter().fetchUserProfile(api);
        return userProfile.getUsername();
    }

    protected static CloseableHttpClient httpClient(String clientId, String clientSecret, String userAgent) {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(50);
        cm.setDefaultMaxPerRoute(50);
        
        CredentialsProvider credp = new BasicCredentialsProvider();
        credp.setCredentials(new AuthScope("ssl.reddit.com", 443), new UsernamePasswordCredentials(clientId,clientSecret));
        
        return HttpClientBuilder.create()
        	.setConnectionManager(cm)
        	.setDefaultCredentialsProvider(credp)
        	.setRetryHandler(new RedditRetryHandler())
        	.setUserAgent(userAgent)
       		.build();
    	
    }
    
    private static class RedditRetryHandler implements HttpRequestRetryHandler {

        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        	logger.debug("Encountered Reddit http error.", exception);
        	
            if (executionCount < 2) {
	            if (exception.getCause() != null && exception.getCause() instanceof HttpServerErrorException) {
	            	logger.info("Recovering from Reddit server error:  " + exception.getCause().getMessage());
	                return true;
	            }
            }
            
            return false;
        }

    }    
    
}
