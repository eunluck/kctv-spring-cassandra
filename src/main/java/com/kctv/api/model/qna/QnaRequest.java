package com.kctv.api.model.qna;

import com.kctv.api.entity.admin.QnaAnswer;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class QnaRequest {

    @ApiModelProperty(readOnly = true,hidden = true)
    private UUID userId;
    @ApiModelProperty(readOnly = true,hidden = true)
    private UUID questionId;
    @ApiModelProperty(notes ="질문내용",required = true)
    private String content;
    @ApiModelProperty(readOnly = true,hidden = true)
    private Date createDt;
    @ApiModelProperty(readOnly = true,hidden = true)
    private Date modifyDt;
    @ApiModelProperty(example = "as", notes="키값 - WiFi A/S:as, WiFi Zone: zone, WakeUf 앱 문의: app, 기타문의: etc" )
    private String questionType;
    @ApiModelProperty(example = "서울시 강서구 마곡나루 육삼빌딩 101호")
    private String address;
    @ApiModelProperty(readOnly = true,hidden = true)
    private String status;
    @ApiModelProperty(required= true)
    private String title;
    @ApiModelProperty(required = true,hidden = true,notes = "사용자 계정에 저장되어있는 이메일")
    private String userEmail;
    @ApiModelProperty(required= true,notes = "사용자 계정에 저장되어있는 닉네임")
    private String userNickname;



}
