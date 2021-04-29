package com.kctv.api.model.place;

import com.google.common.base.Strings;
import com.kctv.api.model.place.openinghours.CloseOrOpen;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

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
public class PlaceInfoEntity {

    @PrimaryKeyColumn(value = "partner_id", type = PrimaryKeyType.PARTITIONED, ordinal = 0)
    @ApiModelProperty(value = "자동으로 생성되는 고유ID", readOnly = true)
    private UUID partnerId;

    @Column("business_name")
    @ApiModelProperty(value = "상호명", readOnly = true)
    private String businessName;

    @Column("partner_address")
    @ApiModelProperty(value = "매장 주소", readOnly = true)
    private String partnerAddress;

    @ApiModelProperty(value = "상세주소", readOnly = true)
    @Column("detailed_address")
    private String detailed_address;

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

    @ApiModelProperty(value = "가게 부가정보", readOnly = true)
    private String placeExplanation;

    @ApiModelProperty(value = "위도", readOnly = true)
    private Long latitude;
    @ApiModelProperty(value = "경도", readOnly = true)
    private Long longitude;
    @ApiModelProperty(value = "서비스타입", readOnly = true)
    @Column("service_type")
    private String serviceType;

    @ReadOnlyProperty
    private boolean surveyCoupon;
    @ReadOnlyProperty
    private boolean placeCoupon;

    public void modifyEntity(PlaceInfoEntity requestPlace){
        if (CollectionUtils.isNotEmpty(requestPlace.getPartnerHomepage()))
            this.partnerHomepage = requestPlace.getPartnerHomepage();
        if (!Strings.isNullOrEmpty(requestPlace.getServiceType()))
            this.serviceType = requestPlace.getServiceType();
        if (CollectionUtils.isNotEmpty(requestPlace.getAges()))
            this.ages = requestPlace.getAges();
        if (!Strings.isNullOrEmpty(requestPlace.getBusinessName()))
            this.businessName = requestPlace.getBusinessName();
        if (CollectionUtils.isNotEmpty(requestPlace.getFacilities()))
            this.facilities = requestPlace.getFacilities();
        if (!Strings.isNullOrEmpty(requestPlace.getPartnerAddress()))
            this.partnerAddress = requestPlace.getPartnerAddress();
        if (CollectionUtils.isNotEmpty(requestPlace.getPeriods()))
            this.periods = requestPlace.getPeriods();
        if (CollectionUtils.isNotEmpty(requestPlace.getPartnerHomepage()))
            this.partnerHomepage = requestPlace.getPartnerHomepage();
        if (!Strings.isNullOrEmpty(requestPlace.getStoreType()))
            this.storeType = requestPlace.getStoreType();
        if (CollectionUtils.isNotEmpty(requestPlace.getTags()))
            this.tags = requestPlace.getTags();
        if (!Strings.isNullOrEmpty(requestPlace.getTelNumber()))
            this.telNumber = requestPlace.getTelNumber();
        if (!Strings.isNullOrEmpty(requestPlace.getStoreParentType()))
            this.storeParentType = requestPlace.getStoreParentType();
        if (!Strings.isNullOrEmpty(requestPlace.getDetailed_address()))
            this.detailed_address = requestPlace.getDetailed_address();
        if (!Strings.isNullOrEmpty(requestPlace.getPlaceExplanation()))
            this.placeExplanation = requestPlace.getPlaceExplanation();
        if (requestPlace.getLatitude() != null && requestPlace.getLatitude() != 0)
            this.latitude = requestPlace.getLatitude();
        if (requestPlace.getLongitude() != null && requestPlace.getLongitude() != 0)
            this.longitude = requestPlace.getLongitude();
        if (CollectionUtils.isNotEmpty(requestPlace.getImages())) {
            this.images = requestPlace.getImages();
        }
    }

    public PlaceInfoEntity(PlaceInfoVo placeInfoVo) {
        copyProperties(placeInfoVo, this);
    }

    public PlaceInfoEntity(PlaceInfoVo placeInfoVo, List<String> images) {
        copyProperties(placeInfoVo, this);
        this.images = images;
    }



    public void couponIsTrue(String couponType){
        if (couponType.equals("survey")){
            this.surveyCoupon = true;
        }else {
            this.placeCoupon = true;
        }

    }

}
