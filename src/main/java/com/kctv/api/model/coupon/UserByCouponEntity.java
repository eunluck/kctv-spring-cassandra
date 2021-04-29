package com.kctv.api.model.coupon;


import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

@Table("user_by_coupon")
@Data
public class UserByCouponEntity {

    @PrimaryKeyColumn(value = "user_id",type = PrimaryKeyType.PARTITIONED)
    private UUID userId;
    @Column("coupon_id")
    private UUID couponId;
    @Column("coupon_name")
    private String couponName;
    @Column("start_date")
    private Date startDate;
    private boolean state;
    @Column("place_name")
    private String placeName;
    @Column("place_id")
    private UUID placeId;

}
