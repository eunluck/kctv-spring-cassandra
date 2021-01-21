package com.kctv.api.repository.user;

import com.kctv.api.entity.user.InviteFriends;
import com.kctv.api.entity.user.UserScrapCard;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface InviteRepository extends CassandraRepository<InviteFriends, UUID> {

    List<InviteFriends> findByUserId(UUID userId); // 나를 추천한 사용자 리스트

    Optional<InviteFriends> findByUserIdAndFriendId(UUID userId,UUID friendId);

}
