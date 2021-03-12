package com.kctv.api.repository.qna;

import com.kctv.api.model.admin.QnaAnswerEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.UUID;

public interface QnaAnswerRepository extends CassandraRepository<QnaAnswerEntity, UUID> {

    List<QnaAnswerEntity> findByQuestionId(UUID id);

}
