package com.kctv.api.entity.place;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@Data
public class MenuVo {


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
