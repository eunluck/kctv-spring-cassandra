package com.kctv.api.repository.user;

import com.kctv.api.model.user.UserInfoByEmailEntity;

import java.util.Optional;

public interface UserByEmailCustomRepository {

    Optional<UserInfoByEmailEntity> findByUserEmailAndUserEmailType(String email, String emailType); //이메일중복체크

    Optional<UserInfoByEmailEntity> findByUserSnsKey(String snsKey);

}
