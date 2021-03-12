package com.kctv.api.repository.user;

import com.kctv.api.model.user.UserInfoEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCustomRepository {

    Optional<UserInfoEntity> findByUserId(UUID id);

    Optional<UserInfoEntity> findByInviteCode(String code);

    List<UserInfoEntity> findAll();
}
