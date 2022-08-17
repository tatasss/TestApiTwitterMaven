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

@Slf4j
@Component
public class KafkaClientImpl implements KafkaClient {
    @Autowired
    @Qualifier("tweeterKafkaTemplate")
    private KafkaTemplate<String,TweetEntity> tweeterKafkaTemplate;

    @Override
    public KafkaResponse sendTweet(TweetEntity tweetEntity) {
        ListenableFuture<SendResult<String, TweetEntity>> future =
                tweeterKafkaTemplate.send("tweet", tweetEntity);
        final KafkaResponse[] kafkaResponse = new KafkaResponse[1];
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
                kafkaResponse[0] = new KafkaResponse(result.getProducerRecord(),result.getRecordMetadata(),"ok");
            }
            @Override
            public void onFailure(Throwable ex) {
                log.error("Unable to send message=["
                        + tweetEntity.getText() + "] due to : " + ex.getMessage());
                kafkaResponse[0] = new KafkaResponse(null,null,"Unable to send message=["
                        + tweetEntity.getText() + "] due to : " + ex.getMessage());            }
        });
        return kafkaResponse[0];
    }
}
