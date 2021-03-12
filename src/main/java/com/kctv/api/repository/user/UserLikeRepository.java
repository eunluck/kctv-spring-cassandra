package com.kctv.api.repository.user;

import com.kctv.api.model.user.UserLikePartnerEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserLikeRepository extends CassandraRepository<UserLikePartnerEntity, UUID> {

    List<UserLikePartnerEntity> findByUserId(UUID userId); // 내가 좋아한 가게 리스트

    Optional<UserLikePartnerEntity> findByUserIdAndPartnerId(UUID userId, UUID partnerId); // 좋아요를 했는지?


}
