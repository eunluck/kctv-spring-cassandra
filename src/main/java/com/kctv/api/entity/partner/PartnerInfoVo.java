package com.kctv.api.entity.partner;

import com.kctv.api.entity.ap.PartnerInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.springframework.beans.BeanUtils.copyProperties;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartnerInfoVo {

    @ApiModelProperty(value = "자동으로 생성되는 고유ID",readOnly = true)
    private UUID partnerId;

    @ApiModelProperty(value = "매장 마감시간",readOnly = true)
    private LocalTime closingTime;

    @ApiModelProperty(value = "상호명",readOnly = true)
    private String businessName;

    @ApiModelProperty(value = "매장 오픈시간",readOnly = true)
    private LocalTime openingTime;

    @ApiModelProperty(value = "매장 주소",readOnly = true)
    private String partnerAddress;

    @ApiModelProperty(value = "매장 관련 홈페이지",readOnly = true)
    private List<String> partnerHomepage;

    @ApiModelProperty(value = "매장 분류(ex:카페,음식점..)",readOnly = true)
    private String storeType;

    @ApiModelProperty(value = "매장 태그",readOnly = true)
    private Set<String> tags;

    @ApiModelProperty(value = "매장 전화번호",readOnly = true)
    private String telNumber;

    @ApiModelProperty(value = "커버이미지",readOnly = true)
    private String coverImage;

    @ApiModelProperty(value = "메뉴",readOnly = true)
    private Map<String,List<MenuByPlace>> menu;

    public PartnerInfoVo(PartnerInfo partnerInfo, Map<String,List<MenuByPlace>> menu) {
        copyProperties(partnerInfo,this);
        this.menu = menu;
    }
}
