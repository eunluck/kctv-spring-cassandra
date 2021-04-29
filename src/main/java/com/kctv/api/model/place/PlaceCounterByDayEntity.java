package com.kctv.api.model.place;


import com.kctv.api.model.stylecard.StyleCardCounterByDayEntity;
import lombok.Data;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.util.UUID;

@Data
@Table("place_counter_by_day")
public class PlaceCounterByDayEntity {


    @PrimaryKey
    private PlaceCounterKey key;

    @Column("like_count")
    @CassandraType(type= CassandraType.Name.COUNTER)
    private Long likeCount;

    @Column("view_count")
    @CassandraType(type= CassandraType.Name.COUNTER)
    private Long viewCount;
/*
    @ReadOnlyProperty
    private String placeName;*/


    @Data
    @PrimaryKeyClass
    public class PlaceCounterKey{
        @PrimaryKeyColumn(value = "update_day",type = PrimaryKeyType.PARTITIONED)
        private Long day;
        @PrimaryKeyColumn(value = "place_id",type = PrimaryKeyType.CLUSTERED)
        private UUID cardId;
    }

}
