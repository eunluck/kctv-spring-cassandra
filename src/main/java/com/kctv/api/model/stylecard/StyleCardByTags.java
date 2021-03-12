package com.kctv.api.model.stylecard;

import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;


@NoArgsConstructor
@Builder
@Getter
@ToString
@Table("style_card_by_tags")
public class StyleCardByTags {

    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED,ordinal = 0)
    private String tag;
    @PrimaryKeyColumn(value ="card_id", type = PrimaryKeyType.CLUSTERED,ordinal = 1)
    private UUID cardId;

    public StyleCardByTags(String tag,UUID cardId) {
        this.cardId = cardId;
        this.tag = tag;
    }

}
