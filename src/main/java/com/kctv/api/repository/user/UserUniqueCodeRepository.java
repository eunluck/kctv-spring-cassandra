package com.kctv.api.repository.user;

import com.kctv.api.model.user.UserUniqueCodeEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.Optional;


public interface UserUniqueCodeRepository extends CassandraRepository<UserUniqueCodeEntity, String> {
    Optional<UserUniqueCodeEntity> findByUniqueCode(String code);

}
