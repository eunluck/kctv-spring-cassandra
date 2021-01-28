package com.kctv.api.repository.user;

import com.kctv.api.entity.user.InviteFriends;
import com.kctv.api.entity.user.UserUniqueCode;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserUniqueCodeRepository extends CassandraRepository<UserUniqueCode, String> {
    Optional<UserUniqueCode> findByUniqueCode(String code);

}
