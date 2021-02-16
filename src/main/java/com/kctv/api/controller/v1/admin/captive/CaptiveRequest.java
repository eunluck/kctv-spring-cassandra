package com.kctv.api.controller.v1.admin.captive;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
public class CaptiveRequest {

    @ApiModelProperty(notes = "이미지 클릭 시 넘어갈 링크")
    private String link;
    @ApiModelProperty(notes = "이미지 파일")
    private MultipartFile imgFile;
    @ApiModelProperty(notes = "광고 시작일")
    private Date startDate;
    @ApiModelProperty(notes = "광고 마지막일")
    private Date endDate;
    @ApiModelProperty(notes = "광고 상태")
    private String status;

}
