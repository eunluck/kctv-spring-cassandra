package com.kctv.api.entity.place;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kctv.api.entity.place.openinghours.CloseOrOpen;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaceInfoVo {

    @ApiModelProperty(value = "상호명")
    private String businessName;

    @ApiModelProperty(value = "매장 주소")
    private String partnerAddress;

    @ApiModelProperty(value = "상세주소", readOnly = true)
    @Column("detailed_address")
    private String detailed_address;

    @ApiModelProperty(value = "매장 관련 홈페이지")
    private List<String> partnerHomepage;

    @ApiModelProperty(value = "매장 분류(ex:카페,음식점..)")
    private String storeType;

    @Column("store_parent_type")
    @ApiModelProperty(value = "매장 대분류(ex:사업장,관광지)", readOnly = true)
    private String storeParentType;


    @ApiModelProperty(value = "매장 태그")
    private Set<String> tags;

    @ApiModelProperty(value = "매장 전화번호")
    private String telNumber;

    @ApiModelProperty(value = "영업시간")
    private List<CloseOrOpen> periods;

    @ApiModelProperty(value = "편의시설")
    private Set<String> facilities;

    @ApiModelProperty(value = "메뉴리스트")
    private List<MenuByPlace> menuList;

    @ApiModelProperty(value = "연령대", readOnly = true)
    private Set<String> ages;

    @ApiModelProperty(value = "위도", readOnly = true)
    private Long latitude;
    @ApiModelProperty(value = "경도", readOnly = true)
    private Long longitude;





}