package com.kctv.api.model.stylecard;

import com.datastax.oss.driver.shaded.guava.common.collect.Sets;
import com.kctv.api.model.admin.stylecard.StyleCardVo;
import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
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
public class StyleCardInfoEntity {

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
    private Set<String> gender;
    private String status;
    @Column("place_id")
    private Set<UUID> placeId = Sets.newHashSet();
    @Column("curator_saying")
    private String curatorSaying;
    private String content;

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public StyleCardInfoEntity(StyleCardVo styleCardVo){
        copyProperties(styleCardVo,this);
        this.createAt = new Date();
        this.cardId = UUID.randomUUID();

    }
    public StyleCardInfoEntity(StyleCardVo styleCardVo, UUID uuid){
        copyProperties(styleCardVo,this);
        this.createAt = new Date();
        this.cardId = uuid;
    }

}
