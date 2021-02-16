package com.kctv.api.entity.admin;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kctv.api.entity.user.UserInfo;
import com.kctv.api.model.tag.Role;
import lombok.*;
import org.springframework.beans.BeanUtils;

import javax.management.relation.RoleNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Setter
@Getter
@AllArgsConstructor
@ToString

public class AddManagerRequest {

    private String managerId;
    private String pwd;
    private String nickname;
    private String email;
    private String userPhone;
    private String role;


    public AddManagerRequest(){}


    public UserInfo newAddManager(Role role){

    return new UserInfo(UUID.randomUUID(),
            "user",
            managerId,
            email,
            null,null,null,null,null,
            nickname,pwd,userPhone,null,"NORMAL",new Date(),null,null,
            role.getAuthority());
    }

    public UserInfo parseUserInfo(UUID usersId) throws RoleNotFoundException {


                 return new UserInfo(usersId,
                "user",
                managerId,
                email,
                null,null,null,null,null,
                nickname,pwd,userPhone,null,"NORMAL",null,new Date(),null,
                         !Strings.isNullOrEmpty(role) ? Role.findAuthorityByDescription(role).getAuthority() : null);



    }

}
