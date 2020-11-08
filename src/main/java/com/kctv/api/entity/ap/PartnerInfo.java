package com.kctv.api.entity.ap;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor


@Table(value = "partner_info")
public class PartnerInfo {

    @PrimaryKeyColumn(value = "partner_id",type = PrimaryKeyType.PARTITIONED,ordinal = 0)
    @ApiModelProperty(value = "자동으로 생성되는 고유ID",readOnly = true)
    private UUID partnerId;

    @Column("closing_time")
    @ApiModelProperty(value = "매장 마감시간",readOnly = true)
    private LocalTime closingTime;

    @Column("business_name")
    @ApiModelProperty(value = "상호명",readOnly = true)
    private String businessName;

    @Column("opening_time")
    @ApiModelProperty(value = "매장 오픈시간",readOnly = true)
    private LocalTime openingTime;

    @Column("partner_address")
    @ApiModelProperty(value = "매장 주소",readOnly = true)
    private String partnerAddress;

    @Column("partner_homepage")
    @ApiModelProperty(value = "매장 관련 홈페이지",readOnly = true)
    private List<String> partnerHomepage;

    @Column("store_type")
    @ApiModelProperty(value = "매장 분류(ex:카페,음식점..)",readOnly = true)
    private String storeType;

    @ApiModelProperty(value = "매장 태그",readOnly = true)
    private List<String> tags;

    @ApiModelProperty(value = "매장 전화번호",readOnly = true)
    @Column("tel_number")
    private String telNumber;
}
