package com.kctv.api.repository.interview;

import com.kctv.api.model.admin.FaqTableEntity;
import com.kctv.api.model.interview.OwnerInterviewEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OwnerInterviewRepository extends CassandraRepository<OwnerInterviewEntity, UUID> {



    Optional<OwnerInterviewEntity> findByPlaceId(UUID id);
    Optional<OwnerInterviewEntity> findByInterviewId(UUID interviewId);

    List<OwnerInterviewEntity> findByInterviewIdIn(List<UUID> interviewId);

}
