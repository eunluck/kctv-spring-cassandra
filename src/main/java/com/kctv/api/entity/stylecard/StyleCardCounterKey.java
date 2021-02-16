/*
package com.kctv.api.entity.stylecard;


import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.util.UUID;

@Data
@PrimaryKeyClass
public class StyleCardCounterKey{
    @PrimaryKeyColumn(value = "update_day",type = PrimaryKeyType.PARTITIONED)
    private Long day;
    @PrimaryKeyColumn(value = "card_id",type = PrimaryKeyType.CLUSTERED)
    private UUID cardId;
}
*/
