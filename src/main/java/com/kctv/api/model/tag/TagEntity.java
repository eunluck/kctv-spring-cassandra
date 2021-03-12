package com.kctv.api.model.tag;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;


@Data
@Builder
@Table(value = "tags")
public class TagEntity {

    @PrimaryKeyColumn(value = "tag_type",ordinal = 0,type = PrimaryKeyType.PARTITIONED)
    private final String tagType;
    @PrimaryKeyColumn(value = "tag_name",ordinal = 1,type = PrimaryKeyType.CLUSTERED)
    private final String tagName;


}
