package com.kctv.api.entity.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "tags")
public class Tag {

    @PrimaryKeyColumn(value = "tag_type",ordinal = 0,type = PrimaryKeyType.PARTITIONED)
    private String tagType;
    @PrimaryKeyColumn(value = "tag_name",ordinal = 1,type = PrimaryKeyType.CLUSTERED)
    private String tagName;


}
