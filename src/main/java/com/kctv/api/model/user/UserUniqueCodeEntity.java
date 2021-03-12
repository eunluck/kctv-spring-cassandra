package com.kctv.api.model.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@Table("user_unique_code")
public class UserUniqueCodeEntity {
    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED,ordinal = 0,value = "unique_code")
    private String uniqueCode;
    @Column("user_id")
    private UUID userId;
    @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED,ordering = Ordering.DESCENDING,ordinal = 1,value = "create_dt")
    private Date createDt;
}
