package com.kctv.api.entity.stylecard;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

@Table("card_image_info")
@Data
@Builder
public class CardImageInfo {

    @PrimaryKeyColumn(value = "image_id",type = PrimaryKeyType.PARTITIONED)
    private UUID imageId;
    @PrimaryKeyColumn(value = "card_id",type = PrimaryKeyType.CLUSTERED)
    private UUID cardId;
    @Column("create_at")
    private Date createAt;
    @Column("file_name")
    private String fileName;
    private String path;
}
