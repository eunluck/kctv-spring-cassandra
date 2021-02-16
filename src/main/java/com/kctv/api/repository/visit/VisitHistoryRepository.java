package com.kctv.api.repository.visit;

import com.kctv.api.entity.user.UserScrapCard;
import com.kctv.api.entity.visit.UserVisitHistoryEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface VisitHistoryRepository extends CassandraRepository<UserVisitHistoryEntity, UUID> {


    List<UserVisitHistoryEntity> findFirst20ByUserId(UUID userId);
    List<UserVisitHistoryEntity> findByUserId(UUID userId);


}
