package com.kctv.api.model.qna;

import com.kctv.api.entity.admin.QnaAnswer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QnaDto {

    private UUID userId;
    private UUID questionId;
    private String content;
    private Date createDt;
    private Date modifyDt;
    private Long latitude;
    private Long longitude;
    private String questionType;
    private String status;
    private String title;
    private String remark;
    private List<QnaAnswer> answers;
    @ApiModelProperty(required = true,hidden = true,notes = "사용자 계정에 저장되어있는 이메일")
    private String userEmail;
    @ApiModelProperty(required= true,notes = "사용자 계정에 저장되어있는 닉네임")
    private String userNickname;

}
