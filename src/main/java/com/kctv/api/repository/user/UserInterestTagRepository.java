package com.kctv.api.repository.user;

import com.kctv.api.model.user.UserInterestTagEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.Optional;
import java.util.UUID;


public interface UserInterestTagRepository extends CassandraRepository<UserInterestTagEntity, UUID> {

    Optional<UserInterestTagEntity>  findByUserId(UUID id);



}
