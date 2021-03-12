package com.kctv.api.repository.user;

import com.kctv.api.model.user.UserEmailVerifyEntity;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserEmailVerifyRepository extends CassandraRepository<UserEmailVerifyEntity, String> {


    Optional<UserEmailVerifyEntity> findByUniqueCode(String uniqueCode);


    @AllowFiltering
    Optional<UserEmailVerifyEntity> findByUserId(UUID user_id);

}
