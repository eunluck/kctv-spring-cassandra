package com.kctv.api.entity.tag;

import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Table("style_card_info")
public class StyleCardInfo {

    @PrimaryKeyColumn(value = "card_id",type = PrimaryKeyType.PARTITIONED)
    private UUID cardId;
    @Column("cover_image")
    private String coverImage;
    @Column("create_at")
    private Date createAt;
    @Column("modify_at")
    private Date modifyAt;
    private String status;
    private Set<String> tags;
    private String title;

}
