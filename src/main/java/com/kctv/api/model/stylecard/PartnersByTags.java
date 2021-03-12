package com.kctv.api.model.stylecard;

import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
@Table("partners_by_tags")
public class PartnersByTags {

    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED,ordinal = 0)
    private String tag;
    @PrimaryKeyColumn(value ="partner_id", type = PrimaryKeyType.CLUSTERED,ordinal = 1)
    private UUID partnerId;


}
