package com.kctv.api.repository.user;

import com.kctv.api.model.user.UserInfoByEmailEntity;
import com.kctv.api.model.user.UserInfoEntity;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserInfoByEmailRepository extends CassandraRepository<UserInfoByEmailEntity, UUID>,UserByEmailCustomRepository {


    Optional<UserInfoByEmailEntity> findByUserEmailAndUserEmailType(String email, String emailType); //이메일중복체크

    Optional<UserInfoByEmailEntity> findByUserSnsKey(String snsKey);



}
