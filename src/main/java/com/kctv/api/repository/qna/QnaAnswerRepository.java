package com.kctv.api.repository.qna;

import com.kctv.api.entity.admin.QnaAnswer;
import com.kctv.api.entity.qna.QnaByUserEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QnaAnswerRepository extends CassandraRepository<QnaAnswer, UUID> {

    List<QnaAnswer> findByQuestionId(UUID id);

}
