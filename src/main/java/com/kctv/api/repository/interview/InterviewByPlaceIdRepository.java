package com.kctv.api.repository.interview;

import com.kctv.api.model.interview.InterviewByPlaceIdEntity;
import com.kctv.api.model.interview.OwnerInterviewEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterviewByPlaceIdRepository extends CassandraRepository<InterviewByPlaceIdEntity, UUID> {

    List<InterviewByPlaceIdEntity> findByPlaceId(UUID id);

}
