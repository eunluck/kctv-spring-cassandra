package com.kctv.api.entity.user;


import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

@Table(value = "invite_friends")
public class InviteFriends {


    @PrimaryKeyColumn(value = "user_id",type = PrimaryKeyType.PARTITIONED,ordinal = 0)
    private UUID userId;

    @PrimaryKeyColumn(value = "friend_id",type = PrimaryKeyType.CLUSTERED,ordinal =1)
    private UUID friendId;

    @PrimaryKeyColumn(value = "create_dt",type = PrimaryKeyType.CLUSTERED,ordering = Ordering.DESCENDING,ordinal =2)
    @Column("create_dt")
    private Date createDt;


}
