package com.kctv.api.repository.user;

import com.kctv.api.entity.user.UserEmailVerify;
import com.kctv.api.entity.user.UserLikePartner;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserEmailVerifyRepository extends CassandraRepository<UserEmailVerify, String> {


    Optional<UserEmailVerify> findByUniqueCode(String uniqueCode);


    @AllowFiltering
    Optional<UserEmailVerify> findByUserId(UUID user_id);

}
