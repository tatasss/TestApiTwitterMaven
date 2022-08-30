package fr.test.service;

import fr.test.client.KafkaClient;
import fr.test.client.TwitterClient;
import fr.test.dao.TweetRepository;
import fr.test.dto.KafkaResponse;
import fr.test.dto.TweetDto;
import fr.test.dto.TwitterResponseDTO;
import fr.test.enumeration.TypeMessagerieBus;
import fr.test.mapper.TweetMapper;
import fr.test.model.TweetEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import scala.Tuple2;
import scala.reflect.ClassTag;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2,2,3600, TimeUnit.MINUTES,new LinkedBlockingDeque<>());
        Arrays.asList(TypeMessagerieBus.KAFKA,TypeMessagerieBus.SPARK).forEach(typeMessagerieBus ->
                threadPoolExecutor.execute(()->{
                    switch (typeMessagerieBus){
                        case KAFKA -> this.pushTweetOnKafka(entities);
                        case SPARK -> this.pushTweetOnSpark(entities);
                    }
                }));

        log.info(" taille des entité {} : ",entities.size());
        tweetRepository.saveAll(entities);
        return  twitterResponseDTO;
    }
    private void pushTweetOnKafka(List<TweetEntity> tweetEntities){
        List<KafkaResponse> kafkaResponses = tweetEntities.stream().map(kafkaClient::sendTweet).toList();
        //mieux utiliser la réponse de kafka
        log.info("fin de l'implementation kafka");
    }
    private void pushTweetOnSpark(List<TweetEntity> tweetEntities){
        SparkConf conf = new SparkConf().setAppName("local[*]").setMaster("spark://master:7077");
        JavaStreamingContext ssc = new JavaStreamingContext(conf, new Duration(1000));
        final List<TweetEntity> tweetSparkEntities= new ArrayList<>();
        //JavaReceiverInputDStream<String> lines = ssc.socketTextStream("localhost", 9092);
        JavaDStream<String> words = (
                (JavaReceiverInputDStream<String>)
                        tweetEntities.stream().map(TweetEntity::getText).collect(Collectors.toList()))
                .flatMap(x -> Arrays.asList(x.split(" ")).iterator());
        JavaPairDStream<String, Integer> pairs = words.mapToPair(s -> new Tuple2<>(s, 1));
        pairs.foreachRDD((rdd)->{ tweetSparkEntities.addAll((Collection<? extends TweetEntity>)
                rdd.rdd().map((Tuple2 <String,Integer>turtle)->{
            TweetEntity tweetEntity = new TweetEntity();
            tweetEntity.setText(turtle._1());
            tweetEntity.setCountWorld(turtle._2());
            tweetEntity.setSpark(true);
            return tweetEntity;
        }, ClassTag.apply(TweetEntity.class)).collect());

        });
        JavaPairDStream<String, Integer> wordCounts = pairs.reduceByKey((i1, i2) -> i1 + i2);
        wordCounts.print();
        // Tweet  ; id , text , count
        ssc.start();              // Start the computation
        try {
            ssc.awaitTermination();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Wait for the computation to terminate
        ssc.close();
        //todo : implementation spark
    }


    public List<TweetDto> getAllTweetBdd(){
        return tweetMapper.tweetEntitysToTweetDtos(tweetRepository.findAll());
    }
    public String postTweet(String text){
        return twitterClient.postAtweet(text);
    }

}
