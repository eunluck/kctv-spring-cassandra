package com.kctv.api.entity.user;


import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

@Table("user_scrap_card")
@Getter
@Builder
public class UserScrapCard {
    public UserScrapCard(UUID userId, UUID cardId) {
        checkNotNull(userId, "유저 UUID를 입력해야 합니다.");
        checkNotNull(cardId,"스타일카드 UUID를 입력해야 합니다.");

        this.userId = userId;
        this.cardId = cardId;
    }

    @PrimaryKeyColumn(value = "user_id",type = PrimaryKeyType.PARTITIONED,ordinal = 0)
    @ApiModelProperty(value = "User ID",readOnly = true)
    private UUID userId;
    @PrimaryKeyColumn(value = "card_id",type = PrimaryKeyType.CLUSTERED,ordinal = 1)
    @ApiModelProperty(value = "Card ID",readOnly = true)
    private UUID cardId;

    
}
