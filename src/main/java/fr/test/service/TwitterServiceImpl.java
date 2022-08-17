package fr.test.service;

import fr.test.client.KafkaClient;
import fr.test.client.TwitterClient;
import fr.test.dao.TweetRepository;
import fr.test.dto.KafkaResponse;
import fr.test.dto.TweetDto;
import fr.test.dto.TwitterResponseDTO;
import fr.test.mapper.TweetMapper;
import fr.test.model.TweetEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;


import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TwitterServiceImpl {
    @Autowired
    private TwitterClient twitterClient;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    KafkaClient kafkaClient;
    private final TweetMapper tweetMapper=TweetMapper.INSTANCE;

    private void sendKafkaTweet(TweetEntity tweetEntity) {


    }
    public TwitterResponseDTO getAllRecentTwitter(String userTwitterId){
        TwitterResponseDTO twitterResponseDTO= twitterClient.getAllRecentTwitter(userTwitterId);
        List<TweetEntity> entities = tweetMapper.tweetDtosToTweetEntitys(twitterResponseDTO.data());
        List<KafkaResponse> kafkaResponses = entities.stream().map(kafkaClient::sendTweet).collect(Collectors.toList());
        log.info(" taille des entité {} : ",entities.size());
        tweetRepository.saveAll(entities);
        return  twitterResponseDTO;
    }


    public List<TweetDto> getAllTweetBdd(){
        return tweetMapper.tweetEntitysToTweetDtos(tweetRepository.findAll());
    }
    public String postTweet(String text){
        return twitterClient.postAtweet(text);
    }

}
