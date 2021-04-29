package com.kctv.api.model.place;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.kctv.api.model.interview.OwnerInterviewEntity;
import com.kctv.api.model.place.openinghours.CloseOrOpen;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.util.CollectionUtils;

import java.time.LocalTime;
import java.util.*;

import static org.springframework.beans.BeanUtils.copyProperties;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceInfoDto {

    @ApiModelProperty(value = "자동으로 생성되는 고유ID",readOnly = true)
    private UUID partnerId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ApiModelProperty(value = "매장 마감시간",readOnly = true)
    private LocalTime closingTime;

    @ApiModelProperty(value = "상호명",readOnly = true)
    private String businessName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ApiModelProperty(value = "매장 오픈시간",readOnly = true)
    private LocalTime openingTime;

    @ApiModelProperty(value = "매장 주소",readOnly = true)
    private String partnerAddress;

    @ApiModelProperty(value = "상세주소", readOnly = true)
    @Column("detailed_address")
    private String detailed_address;

    @ApiModelProperty(value = "매장 관련 홈페이지",readOnly = true)
    private List<String> partnerHomepage;

    @ApiModelProperty(value = "매장 분류(ex:카페,음식점..)",readOnly = true)
    private String storeType;

    @ApiModelProperty(value = "매장 대분류(ex:사업장,관광지)", readOnly = true)
    private String storeParentType;


    @ApiModelProperty(value = "매장 태그",readOnly = true)
    private Set<String> tags;

    @ApiModelProperty(value = "매장 전화번호",readOnly = true)
    private String telNumber;

    @ApiModelProperty(value = "커버이미지",readOnly = true)
    private String coverImage;
    private List<String> images;

    @ApiModelProperty(value = "메뉴",readOnly = true)
    private Map<String,List<MenuByPlaceEntity>> menu;
    @ApiModelProperty(value = "영업시간",readOnly = true)
    private List<CloseOrOpen> periods;
    @ApiModelProperty(value = "영업시간 텍스트",readOnly = true)
    private Map<String,String> weekday_text;
    @ApiModelProperty(value = "편의시설",readOnly = true)
    private Set<String> facilities;
    @ApiModelProperty(value = "연령대", readOnly = true)
    private Set<String> ages;
    @ApiModelProperty(value = "위도", readOnly = true)
    private Long latitude;
    @ApiModelProperty(value = "경도", readOnly = true)
    private Long longitude;
    private String serviceType;
    private String placeExplanation;
    private OwnerInterviewEntity ownerInterview;



    public PlaceInfoDto(PlaceInfoEntity placeInfoEntity, Map<String,List<MenuByPlaceEntity>> menu) {
        copyProperties(placeInfoEntity, this);
        this.menu = menu;

        if(!CollectionUtils.isEmpty(placeInfoEntity.getPeriods()))
        weekday_text = weekText(placeInfoEntity.getPeriods());
    }
    public Map<String,String> weekText(List<CloseOrOpen> original){

        Map<String,String> resultMap = Maps.newLinkedHashMap();
        for (CloseOrOpen oneday : original){
            String korean = null;
            String openingTime = oneday.getOpen().getTime();
            String closingTime = oneday.getClose().getTime();
            switch (oneday.getOpen().getDay()){
                case 1:
                    korean = "월";
                    break;
                case 2:
                    korean = "화";
                    break;
                case 3:
                    korean = "수";
                    break;
                case 4:
                    korean = "목";
                    break;
                case 5:
                    korean = "금";
                    break;
                case 6:
                    korean = "토";
                    break;
                case 7:
                    korean = "일";
                    break;
                case 8:
                    korean = "공휴일";
                    break;
                case 9:
                    korean = "주말";
                    break;
                case 10:
                    korean = "평일";
                    break;
                case 11:
                    korean = "매일";
                    break;
            }
            if(!"close".equals(openingTime) && !Strings.isNullOrEmpty(openingTime) && !Strings.isNullOrEmpty(closingTime)){
            resultMap.put(korean,openingTime.substring(0,2)+":"+ openingTime.substring(openingTime.length() - 2) +" - "+closingTime.substring(0,2)+":"+ closingTime.substring(closingTime.length()-2));
            }else {
                resultMap.put(korean,oneday.getOpen().getTime());
            }
        }
        return resultMap;
    }

}


