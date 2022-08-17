package fr.test.dto;

import fr.test.model.TweetEntity;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public record KafkaResponse(ProducerRecord<String,TweetEntity> producer, RecordMetadata metadata,String message) {
}
