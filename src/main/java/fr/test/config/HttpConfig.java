package fr.test.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class HttpConfig {

    @Autowired
    @Qualifier("twitterAuthorizationInterceptor")
    private ClientHttpRequestInterceptor TwitterAuthorizationInterceptor;

    @Bean
    @Qualifier("twitterRestTemplate")
    public RestTemplate twitterRestTemplate(RestTemplateBuilder builder){
        RestTemplate restTemplate = builder.build();
        initializeInterceptor(restTemplate,TwitterAuthorizationInterceptor);
        return restTemplate;
    }
    private void initializeInterceptor(RestTemplate restTemplate, ClientHttpRequestInterceptor interceptor){
        List<ClientHttpRequestInterceptor> interceptorList = restTemplate.getInterceptors();
        if(interceptorList.isEmpty()){
            interceptorList=new ArrayList<>();
        }
        interceptorList.add(interceptor);
        restTemplate.setInterceptors(interceptorList);
    }
}
