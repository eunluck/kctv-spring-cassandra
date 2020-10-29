package com.kctv.api.model.swagger;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class LoginEx {
    @ApiModelProperty(value = "사용자 이메일", example = "test99@gmail.com")
    private String userEmail;
    @ApiModelProperty(value = "이메일 종류(ex:user,kakao..)",required = true, example = "user")
    private String userEmailType;
    @ApiModelProperty(value = "패스워드",notes = "이메일 타입이 user일때만 입력한다.", example = "0694123")
    private String userPassword;
    @ApiModelProperty(value = "소셜로그인 key",notes = "중복불가. 소셜로그인 시 비밀번호 대신 입력한다.", example = "kakako001")
    private String userSnsKey;

}
