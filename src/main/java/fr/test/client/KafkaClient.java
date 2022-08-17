package fr.test.client;

import fr.test.dto.KafkaResponse;
import fr.test.model.TweetEntity;

public interface KafkaClient {
    KafkaResponse sendTweet(TweetEntity tweetEntity);
}
//todo : creer un topic directement connecter a l'api tweeter
//todo : la consommer ici