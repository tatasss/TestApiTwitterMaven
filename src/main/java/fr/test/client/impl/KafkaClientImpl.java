package fr.test.client.impl;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import fr.test.client.KafkaClient;
import fr.test.dto.KafkaResponse;
import fr.test.model.TweetEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Component
public class KafkaClientImpl implements KafkaClient {
    @Autowired
    @Qualifier("tweeterKafkaTemplate")
    private KafkaTemplate<String,TweetEntity> tweeterKafkaTemplate;

    @Override
    public KafkaResponse sendTweet(TweetEntity tweetEntity){
        ListenableFuture<SendResult<String, TweetEntity>> future =
                tweeterKafkaTemplate.send("tweet", tweetEntity);
        LocalDateTime beforeSenddate= LocalDateTime.now();
        future.addCallback(new ListenableFutureCallback<SendResult<String, TweetEntity>>() {


            @Override
            public void onSuccess(SendResult<String, TweetEntity> result) {
                log.info("Sent message=[" + tweetEntity.getText() +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
                LocalDateTime afterSendDate=LocalDateTime.ofInstant(Instant.ofEpochMilli(result.getRecordMetadata().timestamp()), TimeZone.getDefault().toZoneId());

                Duration diff = Duration.between(afterSendDate,beforeSenddate);
                log.info("durée de l'envoie {} ns ",diff.getNano());
                log.info("durée de l'envoie {} ms ",Math.abs(diff.getNano()/1000000));
                log.info("durée de l'envoie {} s ",diff.getSeconds());

            }
            @Override
            public void onFailure(Throwable ex) {
                log.error("Unable to send message=["
                        + tweetEntity.getText() + "] due to : " + ex.getMessage());
                 }
        });
        try {
            SendResult<String, TweetEntity> result = future.get();
            return new KafkaResponse(result.getProducerRecord(),result.getRecordMetadata(),"ok");
        }catch ( ExecutionException| InterruptedException e){
            log.error("exception when the kafka appel : ",e);
            return new KafkaResponse(null,null, "error for tweet" +tweetEntity.getId()+" : "+e.getMessage());
        }
    }
}
