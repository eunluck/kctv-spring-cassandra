package com.kctv.api.model.visit;

import com.kctv.api.entity.place.PlaceInfo;
import lombok.Data;

import java.util.UUID;

@Data
public class VisitCountDto {

    private UUID userId;
    private PlaceInfo placeInfo;
    private Long visitCount;

    public VisitCountDto(UUID userId, PlaceInfo placeInfo, Long visitCount) {
        this.userId = userId;
        this.placeInfo = placeInfo;
        this.visitCount = visitCount;
    }
}
