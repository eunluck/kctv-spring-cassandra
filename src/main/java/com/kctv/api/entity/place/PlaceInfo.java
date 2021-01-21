package com.kctv.api.entity.place;

import com.kctv.api.entity.place.openinghours.CloseOrOpen;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.springframework.beans.BeanUtils.copyProperties;

@Getter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "partner_info")
public class PlaceInfo {

    @PrimaryKeyColumn(value = "partner_id", type = PrimaryKeyType.PARTITIONED, ordinal = 0)
    @ApiModelProperty(value = "자동으로 생성되는 고유ID", readOnly = true)
    private UUID partnerId;

    @Column("closing_time")
    @ApiModelProperty(value = "매장 마감시간", readOnly = true)
    private LocalTime closingTime;

    @Column("business_name")
    @ApiModelProperty(value = "상호명", readOnly = true)
    private String businessName;

    @Column("opening_time")
    @ApiModelProperty(value = "매장 오픈시간", readOnly = true)
    private LocalTime openingTime;

    @Column("partner_address")
    @ApiModelProperty(value = "매장 주소", readOnly = true)
    private String partnerAddress;

    @Column("partner_homepage")
    @ApiModelProperty(value = "매장 관련 홈페이지", readOnly = true)
    private List<String> partnerHomepage;

    @Column("store_type")
    @ApiModelProperty(value = "매장 분류(ex:카페,음식점..)", readOnly = true)
    private String storeType;


    @Column("store_parent_type")
    @ApiModelProperty(value = "매장 대분류(ex:사업장,관광지)", readOnly = true)
    private String storeParentType;

    @ApiModelProperty(value = "매장 태그", readOnly = true)
    private Set<String> tags;

    @ApiModelProperty(value = "매장 전화번호", readOnly = true)
    @Column("tel_number")
    private String telNumber;

    @ApiModelProperty(value = "커버이미지", readOnly = true)
    @Column("cover_image")
    private String coverImage;


    @ApiModelProperty(value = "영업시간", readOnly = true)
    @Column("opening_hours")
    @CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.UDT, userTypeName = "close_or_open")
    private List<CloseOrOpen> periods;

    @ApiModelProperty(value = "편의시설", readOnly = true)
    @Column("facilities")
    private Set<String> facilities;

    @ApiModelProperty(value = "연령대", readOnly = true)
    private Set<String> ages;

    @ApiModelProperty(value = "시설사진", readOnly = true)
    @Column("images")
    private List<String> images;

    public PlaceInfo(PlaceInfoVo placeInfoVo) {
        copyProperties(placeInfoVo, this);

    }


}
