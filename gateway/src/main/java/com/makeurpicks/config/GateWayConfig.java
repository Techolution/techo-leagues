package com.makeurpicks.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.web.client.RestTemplate;

/**
 * @author tarun
 *
 */
@Configuration
public class GateWayConfig {

	@Autowired
	private OAuth2ClientContext oAuth2ClientContext;

	@Autowired
	private OAuth2ProtectedResourceDetails resource;

	@LoadBalanced
	@Bean(name = "loadBalancedRestTemplate")
	public RestTemplate getRestTemplate(){
		return new RestTemplate();
	}

	@LoadBalanced
	@Bean
	public OAuth2RestOperations getSecureRestTemplate(){
		return new OAuth2RestTemplate(resource,oAuth2ClientContext);
	}


}
