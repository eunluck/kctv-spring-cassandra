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

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "wifi_info")
public class WifiInfo {


    @ApiModelProperty(value = "ap 맥주소",readOnly = true)
    @PrimaryKeyColumn(value = "ap_mac",type = PrimaryKeyType.PARTITIONED)
    private String apMac;

    @Column("ap_address")
    @ApiModelProperty(value = "ap 주소",readOnly = true)
    private String apAddress;

    @Column("ap_lat")
    @ApiModelProperty(value = "위도",readOnly = true)
    private Double apLat;

    @Column("ap_lon")
    @ApiModelProperty(value = "경도",readOnly = true)
    private Double apLon;

    @ApiModelProperty(value = "ap 기기 상태",readOnly = true)
    private String status;

    @ApiModelProperty(value = "ap 기기가 설치된 가게 ID",readOnly = true)
    @Column("partner_id")
    private UUID partnerId;



}
