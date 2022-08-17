package fr.test.client.impl;

import fr.test.client.TwitterClient;
import fr.test.dto.TwitterResponseDTO;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class TwitterClientImpl implements TwitterClient {
    private static final String API="2/tweets/";

    @Value("${twitter.bearer.acces-token}")
    private String accesToken;
    @Value("${twitter.url}")
    private String url;

    @Value("${twitter.bearer.acces-token.secret}")
    private String accesTokenSecret;
    @Value("${twitter.api-key}")
    private String apiKey;

    @Value("${twitter.api-key.secret}")
    private String apiKeySecret;


    @Autowired
    @Qualifier("twitterRestTemplate")
    private RestTemplate twitterRestTemplate;

    private String getUrlFromApi(String path){
        return url+API+path;
    }
    @Override
    public TwitterResponseDTO getAllRecentTwitter(String userTwitter) {
        ResponseEntity<TwitterResponseDTO> reponse =
                twitterRestTemplate.getForEntity( getUrlFromApi("search/recent?query=from:"+userTwitter),
                        TwitterResponseDTO.class);
        return reponse.getBody();
    }

    @Override
    public String postAtweet(String text) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.appendField("text",text);
        HttpEntity<String> entity = new HttpEntity<String>(jsonObject.toJSONString());
        ResponseEntity<String> reponse =
                twitterRestTemplate.postForEntity(getUrlFromApi(""),
                        entity,String.class);
        return reponse.getBody();
    }

}
