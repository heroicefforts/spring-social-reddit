package org.springframework.social.reddit.config.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.xml.ApiHelper;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.reddit.api.Reddit;


/**
 * Support class for JavaConfig and XML configuration support.
 * Creates an API binding instance for the current user's connection.
 * @author Jess Evans
 */
public class RedditApiHelper implements ApiHelper<Reddit> {

	private final UsersConnectionRepository usersConnectionRepository;

	private final UserIdSource userIdSource;

	public RedditApiHelper(UsersConnectionRepository usersConnectionRepository, UserIdSource userIdSource) {
		this.usersConnectionRepository = usersConnectionRepository;
		this.userIdSource = userIdSource;		
	}

	public Reddit getApi() {
		if (logger.isDebugEnabled()) {
			logger.debug("Getting API binding instance for Reddit");
		}
		
		Connection<Reddit> connection = usersConnectionRepository.createConnectionRepository(userIdSource.getUserId()).findPrimaryConnection(Reddit.class);
		if (logger.isDebugEnabled() && connection == null) {
			logger.debug("No current connection; Returning default RedditTemplate instance.");
		}
		return connection != null ? connection.getApi() : null;
	}

	private final static Log logger = LogFactory.getLog(RedditApiHelper.class);

}
