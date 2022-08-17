package fr.test.mapper;

import fr.test.dto.TweetDto;
import fr.test.model.TweetEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-08-17T19:00:59+0200",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 18.0.2 (Oracle Corporation)"
)
public class TweetMapperImpl implements TweetMapper {

    @Override
    public TweetEntity tweetDtoToTweetEntity(TweetDto tweetDto) {
        if ( tweetDto == null ) {
            return null;
        }

        TweetEntity tweetEntity = new TweetEntity();

        tweetEntity.setId( tweetDto.id() );
        tweetEntity.setText( tweetDto.text() );

        return tweetEntity;
    }

    @Override
    public List<TweetEntity> tweetDtosToTweetEntitys(List<TweetDto> tweetDto) {
        if ( tweetDto == null ) {
            return null;
        }

        List<TweetEntity> list = new ArrayList<TweetEntity>( tweetDto.size() );
        for ( TweetDto tweetDto1 : tweetDto ) {
            list.add( tweetDtoToTweetEntity( tweetDto1 ) );
        }

        return list;
    }

    @Override
    public List<TweetDto> tweetEntitysToTweetDtos(List<TweetEntity> tweetDto) {
        if ( tweetDto == null ) {
            return null;
        }

        List<TweetDto> list = new ArrayList<TweetDto>( tweetDto.size() );
        for ( TweetEntity tweetEntity : tweetDto ) {
            list.add( tweetEntityToTweetDto( tweetEntity ) );
        }

        return list;
    }

    protected TweetDto tweetEntityToTweetDto(TweetEntity tweetEntity) {
        if ( tweetEntity == null ) {
            return null;
        }

        String id = null;
        String text = null;

        id = tweetEntity.getId();
        text = tweetEntity.getText();

        TweetDto tweetDto = new TweetDto( id, text );

        return tweetDto;
    }
}
