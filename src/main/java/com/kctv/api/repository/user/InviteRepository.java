package com.kctv.api.repository.user;

import com.kctv.api.model.user.InviteFriendsEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface InviteRepository extends CassandraRepository<InviteFriendsEntity, UUID> {

    List<InviteFriendsEntity> findByUserId(UUID userId); // 나를 추천한 사용자 리스트

    Optional<InviteFriendsEntity> findByUserIdAndFriendId(UUID userId, UUID friendId);

}
