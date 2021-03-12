package com.kctv.api.repository.user;

import com.kctv.api.model.log.AppClkLogEntitiy;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.UUID;

public interface UserLoggingRepository extends CassandraRepository<AppClkLogEntitiy, UUID> {

    List<AppClkLogEntitiy> findByUserId(UUID id);


}
