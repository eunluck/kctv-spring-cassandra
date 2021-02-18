package com.kctv.api.config.database;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.kctv.api.entity.user.UserEmailVerify;
import com.kctv.api.entity.user.UserInfo;
import com.kctv.api.util.AES256Util;
import lombok.SneakyThrows;
import org.springframework.core.Ordered;
import org.springframework.data.cassandra.core.mapping.event.BeforeConvertCallback;

public class EmailVerifyConvertCallback implements BeforeConvertCallback<UserEmailVerify>, Ordered {



    @Override
    public int getOrder() {
        return 99;
    }

    @Override
    @SneakyThrows
    public UserEmailVerify onBeforeConvert(UserEmailVerify userEmailVerify, CqlIdentifier cqlIdentifier) {

        AES256Util aes = AES256Util.getInstance();

        userEmailVerify.setEmail(aes.encrypt(userEmailVerify.getEmail()));
        return userEmailVerify;
    }
}
