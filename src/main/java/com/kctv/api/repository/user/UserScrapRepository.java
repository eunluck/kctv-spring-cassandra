package com.kctv.api.repository.user;

import com.kctv.api.entity.user.UserLikePartner;
import com.kctv.api.entity.user.UserScrapCard;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserScrapRepository extends CassandraRepository<UserScrapCard, UUID> {

    List<UserScrapCard> findByUserId(UUID userId); // 내가 스크랩한 카드 리스트

    Optional<UserScrapCard> findByUserIdAndCardId(UUID userId,UUID cardId); // 스크랩을 했는지?



}
