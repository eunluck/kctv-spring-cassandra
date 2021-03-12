package com.kctv.api.repository.faq;

import com.kctv.api.model.admin.FaqTableEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.Optional;
import java.util.UUID;

public interface FaqRepository extends CassandraRepository<FaqTableEntity, UUID> {



    Optional<FaqTableEntity> findByFaqId(UUID id);

}
