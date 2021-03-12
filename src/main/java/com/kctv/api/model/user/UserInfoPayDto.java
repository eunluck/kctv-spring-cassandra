package com.kctv.api.model.user;

import com.kctv.api.model.payment.PaymentInfoEntity;
import com.kctv.api.model.ap.WakeupPermissionEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
public class UserInfoPayDto extends UserInfoDto {

    private PaymentInfoEntity paymentInfoEntity;


    public UserInfoPayDto(UserInfoEntity userInfoEntity, WakeupPermissionEntity wakeupPermissionEntity, PaymentInfoEntity resultPay) {
        super(userInfoEntity, wakeupPermissionEntity);
        this.paymentInfoEntity = resultPay;
    }
}
