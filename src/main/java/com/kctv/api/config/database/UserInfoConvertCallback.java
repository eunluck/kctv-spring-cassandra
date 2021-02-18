
package com.kctv.api.config.database;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.google.common.base.Strings;
import com.kctv.api.entity.user.UserInfo;
import com.kctv.api.util.AES256Util;
import lombok.SneakyThrows;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.cassandra.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

@Order(1)
@Component
public class UserInfoConvertCallback implements BeforeConvertCallback<UserInfo>, Ordered{
    @SneakyThrows
    @Override
    public UserInfo onBeforeConvert(UserInfo userInfo, CqlIdentifier cqlIdentifier) {

        System.out.println(cqlIdentifier.toString());
        AES256Util aes = AES256Util.getInstance();

        userInfo.setUserEmail(aes.encrypt(userInfo.getUserEmail()));
        if (!Strings.isNullOrEmpty(userInfo.getUserNickname())){
        userInfo.setUserNickname(aes.encrypt(userInfo.getUserNickname()));
        }
        if (!Strings.isNullOrEmpty(userInfo.getUserPhone())){
        userInfo.setUserPhone(aes.encrypt(userInfo.getUserPhone()));
        }

        return userInfo;
    }


    @Override
    public int getOrder() {
        return 100;
    }
}
