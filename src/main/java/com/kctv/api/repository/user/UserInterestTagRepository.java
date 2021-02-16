package com.kctv.api.repository.user;

import com.kctv.api.entity.user.UserInfo;
import com.kctv.api.entity.user.UserInterestTag;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


public interface UserInterestTagRepository extends CassandraRepository<UserInterestTag, UUID> {

    Optional<UserInterestTag>  findByUserId(UUID id);



}
