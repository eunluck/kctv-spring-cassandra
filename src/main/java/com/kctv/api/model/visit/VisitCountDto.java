package com.kctv.api.model.visit;

import com.kctv.api.model.place.PlaceInfoEntity;
import lombok.Data;

import java.util.UUID;

@Data
public class VisitCountDto {

    private UUID userId;
    private PlaceInfoEntity placeInfoEntity;
    private Long visitCount;

    public VisitCountDto(UUID userId, PlaceInfoEntity placeInfoEntity, Long visitCount) {
        this.userId = userId;
        this.placeInfoEntity = placeInfoEntity;
        this.visitCount = visitCount;
    }
}
