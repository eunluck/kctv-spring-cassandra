package com.kctv.api.repository.ap;

import com.kctv.api.model.ap.WakeupPermissionEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.Optional;
import java.util.UUID;

public interface WakeUpPermissionRepository extends CassandraRepository<WakeupPermissionEntity, UUID> {

    Optional<WakeupPermissionEntity> findByUserId(UUID id);

}
