package com.kctv.api.entity.user;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

@Table("user_like_partner")
@Getter
@Builder
public class UserLikePartner {
    public UserLikePartner(UUID userId, UUID partnerId) {
        checkNotNull(userId, "유저 UUID를 입력해야 합니다.");
        checkNotNull(partnerId,"소상공인 UUID를 입력해야 합니다.");

        this.userId = userId;
        this.partnerId = partnerId;
    }

    @PrimaryKeyColumn(value = "user_id",type = PrimaryKeyType.PARTITIONED,ordinal = 0)
    @ApiModelProperty(value = "User ID",readOnly = true)
    private UUID userId;
    @PrimaryKeyColumn(value = "partner_id",type = PrimaryKeyType.CLUSTERED,ordinal = 1)
    @ApiModelProperty(value = "Partner ID",readOnly = true)
    private UUID partnerId;

    
}
