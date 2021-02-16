package com.kctv.api.repository.user;

import com.kctv.api.entity.log.AppClkLog;
import com.kctv.api.entity.user.UserLikePartner;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserLikeRepository extends CassandraRepository<UserLikePartner, UUID> {

    List<UserLikePartner> findByUserId(UUID userId); // 내가 좋아한 가게 리스트

    Optional<UserLikePartner> findByUserIdAndPartnerId(UUID userId,UUID partnerId); // 좋아요를 했는지?


}
