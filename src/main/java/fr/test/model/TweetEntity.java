package fr.test.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("tweet")
@Data
@NoArgsConstructor
public class TweetEntity {

    @Indexed
    private String id;

    private String text;
    private int countWorld;
    private boolean isSpark;
}
