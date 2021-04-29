package com.kctv.api.repository.coupon;

import com.kctv.api.model.coupon.PartnerByCouponEntity;
import com.kctv.api.model.user.InviteFriendsEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface PartnerByCouponRepository extends CassandraRepository<PartnerByCouponEntity, UUID> {

    List<PartnerByCouponEntity> findByPartnerId(UUID partnerId);

}
