package com.kctv.api.repository.coupon;

import com.kctv.api.model.coupon.CouponEntity;
import com.kctv.api.model.coupon.UserByCouponEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.UUID;


public interface UserByCouponRepository extends CassandraRepository<UserByCouponEntity, UUID> {


    List<UserByCouponEntity> findByUserId(UUID userId);

}
