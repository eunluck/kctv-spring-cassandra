package com.kctv.api.entity.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kctv.api.entity.user.UserInfo;
import com.kctv.api.model.tag.Role;
import lombok.Getter;
import lombok.Setter;

import javax.management.relation.RoleNotFoundException;
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

    public ManagerDto(UserInfo userInfo) {

        this.uuid = userInfo.getUserId();
        this.managerId = userInfo.getUserEmail();
        this.pwd = userInfo.getUserPassword();
        this.nickname = userInfo.getUserNickname();
        this.email = userInfo.getUserAddress();
        this.userPhone = userInfo.getUserPhone();
        this.role = findRole(userInfo.getRoles());
        this.roleList = userInfo.getRoles();
    }

    private String findRole(List<String> roles)  {


        return Role.findDescriptionByAuthority(roles);

    }

}
