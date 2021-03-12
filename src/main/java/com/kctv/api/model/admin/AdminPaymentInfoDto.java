
package com.kctv.api.model.admin;

import com.kctv.api.model.ap.WakeupPermissionEntity;
import com.kctv.api.model.user.UserInfoDto;
import com.kctv.api.model.user.UserInfoEntity;
import lombok.Data;

import java.util.List;


@Data
public class AdminPaymentInfoDto extends UserInfoDto {

  private final List<AdminPaymentInfoEntity> adminPaymentInfoEntities;

    public AdminPaymentInfoDto(UserInfoEntity userInfoEntity, WakeupPermissionEntity permission, List<AdminPaymentInfoEntity> adminPaymentInfoEntities) {
        super(userInfoEntity, permission);
        this.adminPaymentInfoEntities = adminPaymentInfoEntities;
    }

}
