package com.kctv.api.repository.qna;

import com.kctv.api.entity.admin.FaqTable;
import com.kctv.api.entity.qna.QnaByUserEntity;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QnaRepository extends CassandraRepository<QnaByUserEntity, UUID> {

    Optional<QnaByUserEntity> findByUserIdAndQuestionId(UUID userId,UUID QuestionId);

    @AllowFiltering
    Optional<QnaByUserEntity> findByQuestionId(UUID QuestionId);

    List<QnaByUserEntity> findByUserId(UUID id);

    @AllowFiltering
    List<QnaByUserEntity> findByStatus(String status);

    @AllowFiltering
    List<QnaByUserEntity> findByQuestionType(String questionType);


}
