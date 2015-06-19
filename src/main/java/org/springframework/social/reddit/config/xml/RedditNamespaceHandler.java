package org.springframework.social.reddit.config.xml;

import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.social.config.xml.AbstractProviderConfigBeanDefinitionParser;
import org.springframework.social.config.xml.AbstractProviderConfigNamespaceHandler;

/**
 * {@link NamespaceHandler} for Spring Social
 * 
 * @author Jess Evans
 */
public class RedditNamespaceHandler extends AbstractProviderConfigNamespaceHandler {

	@Override
	protected AbstractProviderConfigBeanDefinitionParser getProviderConfigBeanDefinitionParser() {
		return new RedditConfigBeanDefinitionParser(); 
	}

}
