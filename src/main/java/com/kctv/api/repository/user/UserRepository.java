package com.kctv.api.repository.user;

import com.kctv.api.entity.user.UserInfo;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Consistency;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CassandraRepository<UserInfo, UUID> {

    Optional<UserInfo> findByUserId(UUID id);

    @AllowFiltering
    Optional<UserInfo> findByUserEmailAndUserEmailType(String email, String emailType); //이메일중복체크


    @AllowFiltering
    Optional<UserInfo> findByUserEmailAndUserEmailTypeAndUserPassword(String email, String emailType, String pwd); //로그인

    @AllowFiltering
    Optional<UserInfo> findByUserSnsKey(String SnsKey);


}
