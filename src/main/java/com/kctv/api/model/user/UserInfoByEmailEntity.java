package com.kctv.api.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("userinfo_by_email")
@AllArgsConstructor
@Data
public class UserInfoByEmailEntity {

    @PrimaryKeyColumn(value = "email",type = PrimaryKeyType.PARTITIONED)
    private String userEmail;
    @PrimaryKeyColumn(value = "email_type",type = PrimaryKeyType.CLUSTERED)
    private String userEmailType;
    @Column("user_id")
    private UUID userId;
    @Column("user_password")
    private String userPassword;
    @Column("user_sns_key")
    private String userSnsKey;


}
