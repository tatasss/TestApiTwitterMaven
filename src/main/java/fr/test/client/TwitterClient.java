package fr.test.client;

import fr.test.dto.TwitterResponseDTO;

public interface TwitterClient {
     TwitterResponseDTO getAllRecentTwitter(String userTwitter);

     String postAtweet(String text);

}
