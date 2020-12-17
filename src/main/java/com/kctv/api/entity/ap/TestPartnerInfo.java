package com.kctv.api.entity.ap;

import com.kctv.api.entity.partner.MenuByPlace;
import com.kctv.api.entity.partner.PartnerInfoVo;
import com.kctv.api.entity.partner.openinghours.CloseOrOpen;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
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
@Builder
@Data
@AllArgsConstructor
@Table(value = "partner_info")
public class TestPartnerInfo {

    private UUID partnerId;
    private LocalTime closingTime;
    private String businessName;
    private LocalTime openingTime;
    private String partnerAddress;
    private List<String> partnerHomepage;
    private String storeType;
    private Set<String> tags;
    private String telNumber;
    private String coverImage;
    private Map<String,List<MenuByPlace>> menu;
    private List<CloseOrOpen> periods;


    public TestPartnerInfo(PartnerInfoVo partnerInfo){
        copyProperties(partnerInfo,this);
    }



}
