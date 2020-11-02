package com.kctv.api.entity.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import springfox.documentation.annotations.ApiIgnore;

import java.util.*;
import java.util.stream.Collectors;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

@Table(value = "user_info")
public class UserInfo implements UserDetails {

    @PrimaryKeyColumn(value = "user_id",type = PrimaryKeyType.PARTITIONED,ordinal = 0)
    @ApiModelProperty(value = "자동으로 생성되는 고유ID",readOnly = true)
    private UUID userId;
    @Column("user_email_type")
    @ApiModelProperty(value = "사용자 이메일 타입",dataType = "String",required = true, example = "user")
    private String userEmailType;

    @ApiModelProperty(value = "사용자 이메일",dataType = "String",required = true, example = "test@gmail.com")
    @Column("user_email")
    private String userEmail;

    @ApiModelProperty(value = "주소",dataType = "String", example = "서울시 용산구")
    @Column("user_address")
    private String userAddress;

    @ApiModelProperty(value = "생일",dataType = "String", example = "19920812")
    @Column("user_birth")
    private String userBirth;

    @ApiModelProperty(value = "성별",dataType = "String", example = "19920812")
    @Column("user_gender")
    private String userGender;

    @ApiModelProperty(value = "사용자 디바이스 맥 주소",dataType = "String", example = "54:EC:2F:3F:71:80")
    @Column("user_mac")
    private List<String> userMac;

    @ApiModelProperty(value = "사용자 이름",dataType = "String", example = "은행운")
    @Column("user_name")
    private String userName;

    @ApiModelProperty(value = "사용자 별명",dataType = "String",required = true, example = "가산동총잡이")
    @Column("user_nickname")
    private String userNickname;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ApiModelProperty(value = "패스워드",notes = "이메일 타입이 user일때만 입력한다.", dataType = "String", example = "0694123")
    @Column("user_password")
    private String userPassword;

    @ApiModelProperty(value = "폰번호", dataType = "String", example = "01023523493")
    @Column("user_phone")
    private String userPhone;

    @ApiModelProperty(value = "정보수집 동의 목록(Map<String,Boolean>)", dataType = "String", example = "{'locationAccept': true, 'marketingAccept': true, 'serviceAccept': true}")
    private Map<String,Boolean> accept;

    @Column("user_status")
    private String userStatus;
    @Column("create_date")
    private Date createDate;
    @Column("update_date")
    @ApiModelProperty(hidden = true)
    private Date updateDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ApiModelProperty(value = "소셜로그인 key",notes = "소셜로그인 시 비밀번호 대신 입력한다.",dataType = "String", example = "qihd2812j3o")
    @Column("user_sns_key")
    private String userSnsKey;

    @ApiModelProperty(hidden = true)
    @Builder.Default
    private List<String> roles = new ArrayList<>();


    @Override
    @ApiModelProperty(hidden = true)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public String getPassword() {
        return null;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public String getUsername() {
        return String.valueOf(this.userId);
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
