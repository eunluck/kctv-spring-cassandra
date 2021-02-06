package com.kctv.api.entity.user;


import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("user_email_verify")
public class UserEmailVerify {

    @PrimaryKeyColumn(value = "unique_code",ordinal = 0,type = PrimaryKeyType.PARTITIONED)
    private String uniqueCode;
    @PrimaryKeyColumn(value = "create_dt",ordinal = 1,type = PrimaryKeyType.CLUSTERED)
    private Date createDt;
    private String email;
    @Column("user_id")
    private UUID userId;


}
