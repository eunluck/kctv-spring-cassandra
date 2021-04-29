package com.kctv.api.model.coupon;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Table("coupon")
public class CouponEntity {

    @PrimaryKeyColumn(value = "coupon_id",type = PrimaryKeyType.PARTITIONED)
    private UUID couponId;
    private List<String> content;
    @Column("place_id")
    private UUID placeId;
    @Column("start_date")
    private Date startDate;
    private boolean state;
    private String title;
}
