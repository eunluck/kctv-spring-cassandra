package com.kctv.api.model.swagger;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;

import java.util.Date;
import java.util.Map;

@Data
public class SignUpEx {



    @ApiModelProperty(value = "사용자 이메일",dataType = "String",required = true, example = "test00@gmail.com")
    private String userEmail;
    @ApiModelProperty(value = "이메일 종류(ex:user,kakao..)",dataType = "String",required = true, example = "user")
    private String userEmailType;
    @ApiModelProperty(value = "사용자 별명",dataType = "String",required = true, example = "가산동총잡이")
    private String userNickname;
    @ApiModelProperty(value = "패스워드",notes = "이메일 타입이 user일때만 입력한다.", dataType = "String", example = "0694123")
    private String userPassword;

    @ApiModelProperty(value = "정보수집 동의 목록(Map<String,Boolean>)",example = "{'locationAccept': true,'marketingAccept': true,'serviceAccept': true}")
    private Map<String,Boolean> accept;

    @ApiModelProperty(value = "소셜로그인 key",notes = "중복불가. 소셜로그인 시 비밀번호 대신 입력한다.",dataType = "String", hidden = true)
    private String userSnsKey;


}
