package com.kctv.api.model.admin;

import com.google.common.base.Strings;
import com.kctv.api.model.user.UserInfoEntity;
import com.kctv.api.model.tag.Role;
import lombok.*;

import javax.management.relation.RoleNotFoundException;
import java.util.Date;
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


    public UserInfoEntity newAddManager(Role role){

    return new UserInfoEntity(UUID.randomUUID(),
            "user",
            managerId,
            email,
            null,null,null,null,null,
            nickname,pwd,userPhone,null,"NORMAL",new Date(),null,null,
            role.getAuthority());
    }

    public UserInfoEntity parseUserInfo(UUID usersId) throws RoleNotFoundException {


                 return new UserInfoEntity(usersId,
                "user",
                managerId,
                email,
                null,null,null,null,null,
                nickname,pwd,userPhone,null,"NORMAL",null,new Date(),null,
                         !Strings.isNullOrEmpty(role) ? Role.findAuthorityByDescription(role).getAuthority() : null);



    }

}
