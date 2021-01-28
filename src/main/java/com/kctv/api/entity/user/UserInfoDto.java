package com.kctv.api.entity.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kctv.api.model.ap.WakeupPermission;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.springframework.beans.BeanUtils.copyProperties;


@Builder
@Data
@AllArgsConstructor
public class UserInfoDto {

    @ApiModelProperty(value = "자동으로 생성되는 고유ID",readOnly = true)
    private UUID userId;

    @ApiModelProperty(value = "사용자 이메일 타입",dataType = "String",required = true, example = "user")
    private String userEmailType;

    @ApiModelProperty(value = "사용자 이메일",dataType = "String",required = true, example = "test@gmail.com")
    private String userEmail;

    @ApiModelProperty(value = "주소",dataType = "String", example = "서울시 용산구")
    private String userAddress;

    @ApiModelProperty(value = "생일",dataType = "String", example = "19920812")
    private String userBirth;

    @ApiModelProperty(value = "성별",dataType = "String", example = "19920812")
    private String userGender;

    @ApiModelProperty(value = "사용자 디바이스 맥 주소",dataType = "String", example = "54:EC:2F:3F:71:80")
    private List<String> userMac;

    @ApiModelProperty(value = "나의 추천 코드",dataType = "String")
    private String inviteCode;

    @ApiModelProperty(value = "사용자 이름",dataType = "String", example = "은행운")
    private String userName;

    @ApiModelProperty(value = "사용자 별명",dataType = "String",required = true, example = "가산동총잡이")
    private String userNickname;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ApiModelProperty(value = "패스워드",notes = "이메일 타입이 user일때만 입력한다.", dataType = "String", example = "0694123")
    private String userPassword;

    @ApiModelProperty(value = "폰번호", dataType = "String", example = "01023523493")
    private String userPhone;

    @ApiModelProperty(value = "정보수집 동의 목록(Map<String,Boolean>)", dataType = "String", example = "{'locationAccept': true, 'marketingAccept': true, 'serviceAccept': true}")
    private Map<String,Boolean> accept;

    private String userStatus;
    private Date createDate;
    @ApiModelProperty(hidden = true)
    private Date updateDate;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ApiModelProperty(value = "소셜로그인 key",notes = "소셜로그인 시 비밀번호 대신 입력한다.",dataType = "String", example = "qihd2812j3o")
    private String userSnsKey;
    @ApiModelProperty(hidden = true)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    private int age;
    private String ages;
    private List<String> tags;

    private WakeupPermission myPermission;

    public UserInfoDto (UserInfo userInfo,List<String> tags,WakeupPermission permission){
        copyProperties(userInfo, this);
        this.tags = tags;
        this.myPermission = permission;
        if (userInfo.getUserBirth() != null && !userInfo.getUserBirth().equals("")) {
            int accAge = ageAcc(userInfo.getUserBirth());
            this.age = accAge;
            this.ages = ages(accAge);
        }
    }

    public UserInfoDto(UserInfo userInfo){
        copyProperties(userInfo,this);
        if(userInfo.getUserBirth() != null && !userInfo.getUserBirth().equals("")) {
            int accAge = ageAcc(userInfo.getUserBirth());
            this.age = accAge;
            this.ages = ages(accAge);
        }
    }


    public UserInfoDto(UserInfo userInfo,List<String> tags) {
        copyProperties(userInfo, this);
            this.tags = tags;
        if (userInfo.getUserBirth() != null && !userInfo.getUserBirth().equals("")) {
            int accAge = ageAcc(userInfo.getUserBirth());
            this.age = accAge;
            this.ages = ages(accAge);
        }
    }




    public int ageAcc(String userBirth){
        LocalDate now = LocalDate.now();
        LocalDate parsedBirthDate = LocalDate.parse(userBirth, DateTimeFormatter.ofPattern("yyyyMMdd"));

        int americanAge = now.minusYears(parsedBirthDate.getYear()).getYear(); // (1)

        // (2)
        // 생일이 지났는지 여부를 판단하기 위해 (1)을 입력받은 생년월일의 연도에 더한다.
        // 연도가 같아짐으로 생년월일만 판단할 수 있다!
        if (parsedBirthDate.plusYears(americanAge).isAfter(now)) {
            americanAge = americanAge -1;
        }

        return americanAge;

    }

    public String ages(int age){

        return String.valueOf(age).substring(0,1) + "0대";
    }



}
