package fr.test.dto;

import java.util.List;

public record TwitterResponseDTO(List<TweetDto>data,MetaDataDto meta) {
}
