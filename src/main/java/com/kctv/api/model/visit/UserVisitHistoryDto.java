package com.kctv.api.model.visit;

import com.kctv.api.model.place.PlaceInfoEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UserVisitHistoryDto {

    private UUID userId;
    private int visitDate;
    private PlaceInfoEntity placeInfoEntity;
    private Date timestamp;
    private String userMac;

    public UserVisitHistoryDto(UserVisitHistoryEntity userVisitHistoryEntity,Long timestamp, PlaceInfoEntity placeInfoEntity){
        BeanUtils.copyProperties(userVisitHistoryEntity,this);
        this.timestamp = new Date(timestamp * 1000L);
        this.placeInfoEntity = placeInfoEntity;
    }
    public UserVisitHistoryDto(UUID userId){
        this.userId = userId;
    }
}
