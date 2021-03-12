/*
package com.kctv.api.config.database;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.google.common.base.Strings;
import com.kctv.api.model.user.UserInfoByEmailEntity;
import com.kctv.api.model.user.UserInfoEntity;
import com.kctv.api.util.AES256Util;
import lombok.SneakyThrows;
import org.springframework.core.Ordered;
import org.springframework.data.cassandra.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

@Component
public class UserInfoByEmailConvertCallback implements BeforeConvertCallback<UserInfoByEmailEntity>, Ordered {
    @Override
    public int getOrder() {
        return 100;
    }

    @SneakyThrows
    @Override
    public UserInfoByEmailEntity onBeforeConvert(UserInfoByEmailEntity userInfoEntity, CqlIdentifier cqlIdentifier) {


        AES256Util aes = AES256Util.getInstance();

        userInfoEntity.setUserEmail(aes.encrypt(userInfoEntity.getUserEmail()));

        if (!Strings.isNullOrEmpty(userInfoEntity.getUserSnsKey())){
            userInfoEntity.setUserSnsKey(aes.encrypt(userInfoEntity.getUserSnsKey()));
        }

        return userInfoEntity;
    }
}
*/
