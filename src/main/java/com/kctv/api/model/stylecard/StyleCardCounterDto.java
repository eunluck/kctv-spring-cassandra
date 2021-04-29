package com.kctv.api.model.stylecard;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Data
@AllArgsConstructor
public class StyleCardCounterDto {

    private UUID cardId;
    private Long scrapCount;
    private Long viewCount;
    @ReadOnlyProperty
    private String cardName;
    @ReadOnlyProperty
    private String coverImage;

    public StyleCardCounterDto (StyleCardCounterByDayEntity styleCardCounterByDayEntity){
        this.cardId = styleCardCounterByDayEntity.getKey().getCardId();
        this.scrapCount = styleCardCounterByDayEntity.getScrapCount() == null ? 0L : styleCardCounterByDayEntity.getScrapCount();
        this.viewCount = styleCardCounterByDayEntity.getViewCount()== null ? 0L : styleCardCounterByDayEntity.getViewCount();;
    }

    public StyleCardCounterDto (StyleCardCounterDto styleCardCounterDto){
        this.cardId = styleCardCounterDto.getCardId();
        this.scrapCount = styleCardCounterDto.getScrapCount() == null ? 0L : styleCardCounterDto.getScrapCount();
        this.viewCount = styleCardCounterDto.getViewCount()== null ? 0L : styleCardCounterDto.getViewCount();
    }

    public StyleCardCounterDto(UUID key, Long value, Long value1) {

        this.cardId = key;
        this.viewCount = value;
        this.scrapCount = value1;
    }
}
