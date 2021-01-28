package com.kctv.api.entity.admin.ad;


import lombok.*;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table("captive_portal_ad")
public class CaptivePortalAdEntity {

    @PrimaryKeyColumn(value = "ad_id",type = PrimaryKeyType.PARTITIONED,ordinal = 0)
    private UUID adId;
    @PrimaryKeyColumn(value = "ad_create_dt",type = PrimaryKeyType.CLUSTERED,ordinal = 1)
    private Date adCreateDt;
    @Column("ad_modify_dt")
    private Date adModifyDt;
    @Column("ad_status")
    private String adStatus;
    @Column("img_name")
    private String imgName;
    @Column("img_path")
    private String imgPath;
    @Column("ad_link")
    private String adLink;
    @Column("ad_start_dt")
    private Date adStartDt;
    @Column("ad_end_dt")
    private Date adEndDt;
    @Column("img_url")
    private String imgUrl;


}
