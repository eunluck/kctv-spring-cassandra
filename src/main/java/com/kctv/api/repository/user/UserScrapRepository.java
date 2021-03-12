package com.kctv.api.repository.user;

import com.kctv.api.model.user.UserScrapCardEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserScrapRepository extends CassandraRepository<UserScrapCardEntity, UUID> {

    List<UserScrapCardEntity> findByUserId(UUID userId); // 내가 스크랩한 카드 리스트

    Optional<UserScrapCardEntity> findByUserIdAndCardId(UUID userId, UUID cardId); // 스크랩을 했는지?



}
