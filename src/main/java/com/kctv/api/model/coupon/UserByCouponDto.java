package com.kctv.api.model.coupon;


import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class UserByCouponDto {

    private UUID placeId;
    private UUID couponId;
    private String placeName;
    private Date startDate;
    private Date expireDate;
    private boolean state;
    private String couponName;

    public UserByCouponDto(UserByCouponEntity userByCouponEntity){
        this.couponId = userByCouponEntity.getCouponId();
        this.startDate = userByCouponEntity.getStartDate();
        this.couponName = userByCouponEntity.getCouponName();
        this.placeId = userByCouponEntity.getPlaceId();
        this.placeName = userByCouponEntity.getPlaceName();
        this.expireDate = new Date(startDate.toInstant().plus(30,ChronoUnit.DAYS).toEpochMilli());
        this.state = couponStateCheck(userByCouponEntity);
    }


    public boolean couponStateCheck(UserByCouponEntity userByCouponEntity){
        if (userByCouponEntity.isState()){
            return Instant.ofEpochMilli(System.currentTimeMillis()).isBefore(this.expireDate.toInstant());
        }else return false;

    }


}
