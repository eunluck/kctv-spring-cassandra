package com.kctv.api.model.coupon;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.beans.BeanUtils.copyProperties;

@Data
public class CouponByPlaceDto {

    private UUID couponId;
    private List<String> content;
    private UUID placeId;
    private Date startDate;
    private boolean canSaved;
    private String title;


    public CouponByPlaceDto(CouponEntity couponEntity){

        copyProperties(couponEntity,this);


    }


}
