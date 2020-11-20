package com.kctv.api.entity.user;

import com.kctv.api.entity.tag.Tag;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;

import java.util.Set;
import java.util.UUID;

@Table
@Builder
@Getter
public class UserInterestTag {

    @PrimaryKeyColumn(value = "user_id",type = PrimaryKeyType.PARTITIONED)
    private UUID userId;
    @Column("modify_at")
    private Date modifyAt;
    private Set<Tag> tags;

}
