package com.kctv.api.model.swagger;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.util.Map;
import java.util.UUID;


@Data
public class UserUpdateEx {

    @ApiModelProperty(value = "사용자 주소",dataType = "String", example = "서울시 강복동 수정후")
    private String userAddress;
    @ApiModelProperty(value = "생일",dataType = "String", example = "19920812")
    private String userBirth;
    @ApiModelProperty(value = "성별",dataType = "String", example = "남")
    private String userGender;
    @ApiModelProperty(value = "디바이스 맥 주소",dataType = "String", example = "54:EC:2F:3F:71:80")
    private String userMac;
    @ApiModelProperty(value = "사용자 별명",dataType = "String", example = "별명수정해")
    private String userNickname;
    @ApiModelProperty(value = "사용자 비밀번호",dataType = "String", example = "0694123")
    private String userPassword;
    @ApiModelProperty(value = "핸드폰 번호",dataType = "String", example = "01023523943")
    private String userPhone;
    @ApiModelProperty(value = "이용 동의", dataType = "Map", example = "{'locationAccept': true, 'marketingAccept': true, 'serviceAccept': true}")
    private Map<String,Boolean> accept;

}
