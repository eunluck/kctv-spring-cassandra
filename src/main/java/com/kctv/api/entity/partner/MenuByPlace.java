package com.kctv.api.entity.partner;

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
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@Data
@AllArgsConstructor
@Table(value = "menu_by_partner")
public class MenuByPlace {

    @PrimaryKeyColumn(value = "partner_id",type = PrimaryKeyType.PARTITIONED,ordinal = 0)
    @ApiModelProperty(value = "가게 ID",readOnly = true)
    private UUID partnerId;

    @PrimaryKeyColumn(value = "menu_type",type = PrimaryKeyType.CLUSTERED, ordinal = 1)
    @ApiModelProperty(value = "메뉴 카테고리",readOnly = true)
    private String menuType;

    @PrimaryKeyColumn(value = "menu_name",type = PrimaryKeyType.CLUSTERED, ordinal = 2)
    @ApiModelProperty(value = "메뉴이름",readOnly = true)
    private String menuName;

    @Column("menu_description")
    @ApiModelProperty(value = "메뉴 설명",readOnly = true)
    private String menuDescription;

    @Column("menu_image")
    @ApiModelProperty(value = "메뉴 이미지 주소",readOnly = true)
    private String menuImage;

    @Column("menu_price")
    @ApiModelProperty(value = "메뉴 가격",readOnly = true)
    private Long menuPrice;


}
