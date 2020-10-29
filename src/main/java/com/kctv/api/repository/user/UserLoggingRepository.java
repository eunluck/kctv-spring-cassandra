package com.kctv.api.repository.user;

import com.kctv.api.entity.log.AppClkLog;
import com.kctv.api.entity.user.UserInfo;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserLoggingRepository extends CassandraRepository<AppClkLog, UUID> {

    List<AppClkLog> findByUserId(UUID id);


}
