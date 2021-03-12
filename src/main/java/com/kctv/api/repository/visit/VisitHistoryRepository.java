package com.kctv.api.repository.visit;

import com.kctv.api.model.visit.UserVisitHistoryEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.UUID;


public interface VisitHistoryRepository extends CassandraRepository<UserVisitHistoryEntity, UUID> {


    List<UserVisitHistoryEntity> findFirst20ByUserId(UUID userId);
    List<UserVisitHistoryEntity> findByUserId(UUID userId);


}
