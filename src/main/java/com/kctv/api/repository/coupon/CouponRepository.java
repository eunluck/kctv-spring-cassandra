package com.kctv.api.repository.coupon;

import com.kctv.api.model.coupon.CouponEntity;
import com.kctv.api.model.coupon.PartnerByCouponEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.UUID;


public interface CouponRepository extends CassandraRepository<CouponEntity, UUID> {

    CouponEntity findByCouponId(UUID couponId);
    List<CouponEntity> findByCouponIdIn(List<UUID> couponIds);

}
