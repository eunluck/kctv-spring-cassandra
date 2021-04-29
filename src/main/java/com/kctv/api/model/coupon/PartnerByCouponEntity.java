package com.kctv.api.model.coupon;


import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("partner_by_coupon")
@Data
public class PartnerByCouponEntity {

    @PrimaryKeyColumn(value = "partner_id",type = PrimaryKeyType.PARTITIONED)
    private UUID partnerId;
    @Column("coupon_id")
    private UUID couponId;
    @Column("coupon_type")
    private String couponType;
    private String state;

}
