package com.kctv.api.config.database;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.google.common.base.Strings;
import com.kctv.api.model.user.UserInfoEntity;
import com.kctv.api.util.AES256Util;
import lombok.SneakyThrows;
import org.springframework.core.Ordered;
import org.springframework.data.cassandra.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

@Component
public class UserInfoConvertCallback implements BeforeConvertCallback<UserInfoEntity>, Ordered {
    @Override
    public int getOrder() {
        return 100;
    }

    @SneakyThrows
    @Override
    public UserInfoEntity onBeforeConvert(UserInfoEntity userInfoEntity, CqlIdentifier cqlIdentifier) {


        System.out.println(cqlIdentifier.toString());
        AES256Util aes = AES256Util.getInstance();

        userInfoEntity.setUserEmail(aes.encrypt(userInfoEntity.getUserEmail()));
        if (!Strings.isNullOrEmpty(userInfoEntity.getUserNickname())){
            userInfoEntity.setUserNickname(aes.encrypt(userInfoEntity.getUserNickname()));
        }
        if (!Strings.isNullOrEmpty(userInfoEntity.getUserPhone())){
            userInfoEntity.setUserPhone(aes.encrypt(userInfoEntity.getUserPhone()));
        }
        if (!Strings.isNullOrEmpty(userInfoEntity.getUserSnsKey())){
            userInfoEntity.setUserSnsKey(aes.encrypt(userInfoEntity.getUserSnsKey()));
        }

        return userInfoEntity;
    }
}
