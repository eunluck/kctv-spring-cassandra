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
@Table("style_card_counter")
public class StyleCardCounterEntity {

    @PrimaryKeyColumn(value = "card_id",type = PrimaryKeyType.PARTITIONED)
    private UUID cardId;

    @Column("scrap_count")
    @CassandraType(type= CassandraType.Name.COUNTER)
    private Long scrapCount;

    @Column("view_count")
    @CassandraType(type= CassandraType.Name.COUNTER)
    private Long viewCount;

    @ReadOnlyProperty
    private String cardName;

}
