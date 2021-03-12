package com.kctv.api.repository.user;

import com.kctv.api.model.user.UserInfoEntity;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.Optional;
import java.util.UUID;
public interface UserRepository extends CassandraRepository<UserInfoEntity, UUID>,UserCustomRepository {

    Optional<UserInfoEntity> findByUserId(UUID id);
/*

    @AllowFiltering
    Optional<UserInfoEntity> findByUserEmailAndUserEmailType(String email, String emailType); //이메일중복체크


    @AllowFiltering
    Optional<UserInfoEntity> findByUserSnsKey(String SnsKey);
*/


    @AllowFiltering
    Optional<UserInfoEntity> findByInviteCode(String code);


}
