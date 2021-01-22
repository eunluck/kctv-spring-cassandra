package com.kctv.api.entity.visit;

import lombok.Data;

import java.util.UUID;

@Data
public class VisitCount {

    private UUID userId;
    private UUID placeId;
    private Long visitCount;

    public VisitCount(UUID userId, UUID placeId, Long visitCount) {
        this.userId = userId;
        this.placeId = placeId;
        this.visitCount = visitCount;
    }
}
