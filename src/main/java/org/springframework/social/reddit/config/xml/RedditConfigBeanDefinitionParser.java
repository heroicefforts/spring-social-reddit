package org.springframework.social.reddit.config.xml;

import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.social.config.xml.AbstractProviderConfigBeanDefinitionParser;
import org.springframework.social.reddit.config.support.RedditApiHelper;
import org.springframework.social.reddit.connect.RedditConnectionFactory;
import org.springframework.social.reddit.security.RedditAuthenticationService;
import org.springframework.social.security.provider.SocialAuthenticationService;

/**
 * Implementation of {@link AbstractConnectionFactoryBeanDefinitionParser} that creates a {@link RedditConnectionFactory}.
 * @author Jess Evans
 */
class RedditConfigBeanDefinitionParser extends AbstractProviderConfigBeanDefinitionParser {

	public RedditConfigBeanDefinitionParser() {
		super(RedditConnectionFactory.class, RedditApiHelper.class);
	}
	
	@Override
	protected Class<? extends SocialAuthenticationService<?>> getAuthenticationServiceClass() {
		return RedditAuthenticationService.class;
	}
	
	@Override
	protected BeanDefinition getConnectionFactoryBeanDefinition(String appId, String appSecret, Map<String, Object> allAttributes) {
		return BeanDefinitionBuilder.genericBeanDefinition(RedditConnectionFactory.class)
				.addConstructorArgValue(appId)
				.addConstructorArgValue(appSecret)
				.addConstructorArgValue(allAttributes.get("user-agent"))
				.getBeanDefinition();
	}
	
	@Override
	protected BeanDefinition getAuthenticationServiceBeanDefinition(String appId, String appSecret, Map<String, Object> allAttributes) {
		return BeanDefinitionBuilder.genericBeanDefinition(authenticationServiceClass)
				.addConstructorArgValue(appId)
				.addConstructorArgValue(appSecret)
				.addConstructorArgValue(allAttributes.get("user-agent"))
				.getBeanDefinition();
	}	

}