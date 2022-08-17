package fr.test.mapper;

import fr.test.dto.TweetDto;
import fr.test.model.TweetEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TweetMapper {
    TweetMapper INSTANCE = Mappers.getMapper(TweetMapper.class);
    TweetEntity tweetDtoToTweetEntity(TweetDto tweetDto);
    List<TweetEntity> tweetDtosToTweetEntitys(List<TweetDto> tweetDto);
    List<TweetDto> tweetEntitysToTweetDtos(List<TweetEntity> tweetDto);
}
