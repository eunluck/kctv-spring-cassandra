package com.kctv.api.entity.place;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Data
@Builder
@Table("place_type")
public class PlaceTypeEntity {


    @PrimaryKeyColumn(value = "large",type = PrimaryKeyType.PARTITIONED)
    private String placeParentType;
    @PrimaryKeyColumn(value = "medium",type = PrimaryKeyType.CLUSTERED)
    private String placeType;


}
