package fr.test.interceptor;

import fr.test.dto.TwitterAuthenticateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

@Component("twitterAuthorizationInterceptor")
public class TwitterAuthorizationInterceptor implements ClientHttpRequestInterceptor {
    @Value("${twitter.bearer.acces-token}")
    private String accesToken;

    @Value("${twitter.bearer.acces-token.secret}")
    private String accesTokenSecret;
    @Value("${twitter.api-key}")
    private String apiKey;

    @Value("${twitter.api-key.secret}")
    private String apiKeySecret;

    @Value("${twitter.url}")
    private String url;

    @Value("${twitter.bearer.token}")
    private String token;

    private String getAuthenticated(){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization","Basic "+ Base64.getEncoder()
                .encodeToString((apiKey+":"+apiKeySecret).getBytes()));
        httpHeaders.add("content-type","application/x-www-form-urlencoded;charset=UTF-8");
        HttpEntity<String> entity = new HttpEntity<String>("grant_type=client_credentials",httpHeaders);
        ResponseEntity<TwitterAuthenticateDto> response = restTemplate.postForEntity(url+"oauth2/token",entity,TwitterAuthenticateDto.class);
        return Objects.requireNonNull(response.getBody()).access_token();
    }
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
         request.getHeaders().set("authorization"," bearer "+getAuthenticated());
         //request.getHeaders().set("Accept-encoding","gzip");
         return execution.execute(request, body);
    }
}
