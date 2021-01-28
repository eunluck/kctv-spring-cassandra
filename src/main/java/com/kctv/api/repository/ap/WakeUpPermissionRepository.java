package com.kctv.api.repository.ap;

import com.kctv.api.entity.place.WifiInfo;
import com.kctv.api.model.ap.WakeupPermission;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WakeUpPermissionRepository extends CassandraRepository<WakeupPermission, UUID> {

    Optional<WakeupPermission> findByUserId(UUID id);

}
