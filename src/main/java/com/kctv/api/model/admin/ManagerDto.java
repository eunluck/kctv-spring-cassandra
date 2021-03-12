package com.kctv.api.model.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kctv.api.model.user.UserInfoEntity;
import com.kctv.api.model.tag.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ManagerDto {

    private UUID uuid;
    private String managerId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String pwd;
    private String nickname;
    private String email;
    private String userPhone;
    private String role;
    private List<String> roleList;

    public ManagerDto(String managerId, String pwd, String nickname, String email, String userPhone, String role) {
        this.managerId = managerId;
        this.pwd = pwd;
        this.nickname = nickname;
        this.email = email;
        this.userPhone = userPhone;
        this.role = role;
    }

    public ManagerDto(UserInfoEntity userInfoEntity) {

        this.uuid = userInfoEntity.getUserId();
        this.managerId = userInfoEntity.getUserEmail();
        this.pwd = userInfoEntity.getUserPassword();
        this.nickname = userInfoEntity.getUserNickname();
        this.email = userInfoEntity.getUserAddress();
        this.userPhone = userInfoEntity.getUserPhone();
        this.role = findRole(userInfoEntity.getRoles());
        this.roleList = userInfoEntity.getRoles();
    }

    private String findRole(List<String> roles)  {


        return Role.findDescriptionByAuthority(roles);

    }

}
