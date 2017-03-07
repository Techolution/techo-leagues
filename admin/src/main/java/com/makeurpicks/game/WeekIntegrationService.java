package com.makeurpicks.game;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import rx.Observable;

@Service
public class WeekIntegrationService {

private Log log = LogFactory.getLog(WeekIntegrationService.class);

	
	@Autowired
    @Qualifier("loadBalancedRestTemplate")
    @LoadBalanced
    @Bean(name={"loadBalancedRestTemplate"})
	RestTemplate restTemplate() {
		  return new RestTemplate();
		 }
	
	@Autowired
	AuthorizationCodeResourceDetails oAuth2ProtectedResourceDetails;
	@Autowired
	OAuth2ClientContext oAuth2ClientContext;
	 
    @LoadBalanced
    @Bean
    private OAuth2RestOperations secureRestTemplate(){
		return new OAuth2RestTemplate(oAuth2ProtectedResourceDetails, oAuth2ClientContext);
	}
	
	public Observable<List<WeekView>> getWeeksForSeason(String id)
	{
		return new WeeksBySeasonObservableCommand(id, secureRestTemplate()).observe();
	}
	
//	@HystrixCommand(fallbackMethod = "stubWeeks",
//            commandProperties = {
//                    @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
////                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
//            }
//    )
//    public Observable<List<WeekView>> getWeeksForSeason(String id) {
//        return new ObservableResult<List<WeekView>>() {
//            @Override
//            public List<WeekView> invoke() {
//            	ParameterizedTypeReference<List<WeekView>> responseType = new ParameterizedTypeReference<List<WeekView>>() {};
//            	List<WeekView> weeks = secureRestTemplate.exchange("http://game/weeks/seasonid/{id}", HttpMethod.GET, null, responseType, id).getBody();
//            	return weeks;
////            	return Observable.just(weeks);
//                                
//            }
//        };
//    }
//
//    @SuppressWarnings("unused")
//    private List<WeekView> stubWeeks(final String weekId) {
//    	WeekView stub = new WeekView();
//    	stub.setWeekNumber(0);
//    	stub.setId(weekId);
//        return Arrays.asList(stub);
//    }
//    
    public WeekView createWeek(WeekView weekView)
    {
    	return secureRestTemplate().postForEntity("http://game/weeks/", weekView, WeekView.class).getBody();
    }
}
