package com.kctv.api.entity.stylecard.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.ws.BindingType;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

@Data
public class StyleCardVo {

    @ApiModelProperty(value = "제목", required = true)
    private String title;
    @ApiModelProperty(value = "카드ID",readOnly = true)
    private UUID cardId;
    @ApiModelProperty(value = "연령대", required = true)
    private Set<String> ages;
    @ApiModelProperty(value = "태그", required = true)
    private Set<String> tags;
    @ApiModelProperty(value = "성별()", required = true)
    private Set<String> gender;
    @ApiModelProperty(value = "장소 ID", required = true)
    private Set<UUID> placeId;
    @ApiModelProperty(value = "커버이미지 URL",readOnly = true)
    private String coverImage;
    @ApiModelProperty(value = "카드 상태")
    private String status;
    @ApiModelProperty(value = "생성일자",readOnly = true)
    private Date createAt;
    @ApiModelProperty(value = "수정일자",readOnly = true)
    private Date modifyAt;
    @ApiModelProperty(value = "큐레이터의 소갯말")
    private String curatorSaying;



}
