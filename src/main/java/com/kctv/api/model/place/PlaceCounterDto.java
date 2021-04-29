package com.kctv.api.model.place;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PlaceCounterDto {

    private UUID placeId;
    private Long likeCount;
    private Long viewCount;
    @ReadOnlyProperty
    private String placeName;
    @ReadOnlyProperty
    private String placeType;
    @ReadOnlyProperty
    private String coverImage;



    public PlaceCounterDto(PlaceCounterByDayEntity placeCounterByDayEntity){
        this.placeId = placeCounterByDayEntity.getKey().getCardId();
        this.likeCount = placeCounterByDayEntity.getLikeCount() == null ? 0 : placeCounterByDayEntity.getLikeCount();
        this.viewCount = placeCounterByDayEntity.getViewCount() == null ? 0 : placeCounterByDayEntity.getViewCount();
    }

    public PlaceCounterDto(UUID key, Long key1, Long value) {
        this.placeId = key;
        this.viewCount =key1;
        this.likeCount = value;
    }

}
