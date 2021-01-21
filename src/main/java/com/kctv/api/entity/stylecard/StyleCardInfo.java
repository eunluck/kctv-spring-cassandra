package com.kctv.api.entity.stylecard;

import com.kctv.api.entity.stylecard.admin.StyleCardVo;
import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.springframework.beans.BeanUtils.copyProperties;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table("style_card_info")
public class StyleCardInfo {

    private String title;
    @PrimaryKeyColumn(value = "card_id",type = PrimaryKeyType.PARTITIONED)
    private UUID cardId;
    @Column("create_at")
    private Date createAt;
    @Column("modify_at")
    private Date modifyAt;
    @Column("cover_image")
    private String coverImage;
    private Set<String> tags;
    private Set<String> ages;
    private String status;
    @Column("place_id")
    private Set<UUID> placeId;
    @Column("curator_saying")
    private String curatorSaying;


    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public StyleCardInfo(StyleCardVo styleCardVo){
        copyProperties(styleCardVo,this);
        this.createAt = new Date();
        this.cardId = UUID.randomUUID();

    }
}
