package fr.test.controler;

import fr.test.dto.TweetDto;
import fr.test.dto.TwitterResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

public interface TwitterController {
     ResponseEntity<TwitterResponseDTO> getRecentTwitterfromUserId(String UserId);
     ResponseEntity<List<TweetDto>> getAllTweetFromBdd();

     ResponseEntity<String> postAtweet(String text);

}
