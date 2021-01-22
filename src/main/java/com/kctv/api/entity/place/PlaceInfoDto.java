package com.kctv.api.entity.place;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import com.kctv.api.entity.place.openinghours.CloseOrOpen;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
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
    private Map<String,List<MenuByPlace>> menu;
    @ApiModelProperty(value = "영업시간",readOnly = true)
    private List<CloseOrOpen> periods;
    @ApiModelProperty(value = "영업시간 텍스트",readOnly = true)
    private Map<String,String> weekday_text;
    @ApiModelProperty(value = "편의시설",readOnly = true)
    private Set<String> facilities;
    @ApiModelProperty(value = "연령대", readOnly = true)
    private Set<String> ages;


    public PlaceInfoDto(PlaceInfo placeInfo, Map<String,List<MenuByPlace>> menu) {
        copyProperties(placeInfo, this);
        this.menu = menu;

        if(CollectionUtils.isEmpty(placeInfo.getPeriods()))
        weekday_text = weekText(placeInfo.getPeriods());
    }
    public Map<String,String> weekText(List<CloseOrOpen> original){

        Map<String,String> resultMap = Maps.newLinkedHashMap();
        for (CloseOrOpen oneday : original){
            String korean = null;
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
            }
            if(!"close".equals(oneday.getOpen().getTime())){
            resultMap.put(korean,oneday.getOpen().getTime().substring(0,2)+":"+ oneday.getOpen().getTime().substring(2,4) +" - "+oneday.getClose().getTime().substring(0,2)+":"+ oneday.getClose().getTime().substring(2,4));
            }else {
                resultMap.put(korean,oneday.getOpen().getTime());
            }
        }
        return resultMap;
    }

}


