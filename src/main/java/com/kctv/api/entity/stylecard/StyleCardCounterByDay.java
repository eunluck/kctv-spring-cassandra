package com.kctv.api.entity.stylecard;

import lombok.Data;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.util.UUID;

@Data
@Table("style_card_counter_by_day")
public class StyleCardCounterByDay {

    @PrimaryKey
    private StyleCardCounterKey key;

    @Column("scrap_count")
    @CassandraType(type= CassandraType.Name.COUNTER)
    private Long scrapCount;

    @Column("view_count")
    @CassandraType(type= CassandraType.Name.COUNTER)
    private Long viewCount;

    @ReadOnlyProperty
    private String cardName;


    @Data
    @PrimaryKeyClass
    public class StyleCardCounterKey{
        @PrimaryKeyColumn(value = "update_day",type = PrimaryKeyType.PARTITIONED)
        private Long day;
        @PrimaryKeyColumn(value = "card_id",type = PrimaryKeyType.CLUSTERED)
        private UUID cardId;
    }

}
