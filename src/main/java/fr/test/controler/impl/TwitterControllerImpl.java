package fr.test.controler.impl;

import fr.test.controler.TwitterController;
import fr.test.dto.TweetDto;
import fr.test.service.TwitterServiceImpl;
import fr.test.dto.TwitterResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/twitter")
@Slf4j
public class TwitterControllerImpl implements TwitterController {
    @Autowired
    TwitterServiceImpl twitterService;

    @Override
    @GetMapping(value = "/{userId}")
    public ResponseEntity<TwitterResponseDTO> getRecentTwitterfromUserId(@PathVariable("userId")  String userId) {
        log.info("appel de getAllRecentTweetFromUser pour le @{}",userId);
        return ResponseEntity.ok(twitterService.getAllRecentTwitter(userId));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<TweetDto>> getAllTweetFromBdd() {
        log.info("récupération de tous les twitter dans la base de donnée");
        return ResponseEntity.ok(twitterService.getAllTweetBdd());
    }

    @Override
    //@PostMapping("/tweet")
    public ResponseEntity<String> postAtweet(@RequestBody String text) {
        return ResponseEntity.ok(twitterService.postTweet(text));
    }



}
