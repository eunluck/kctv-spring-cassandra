package com.kctv.api.entity.payment;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("payment_code")
@Data
public class PaymentCode {

    @PrimaryKeyColumn(value = "app_payment_code",type = PrimaryKeyType.PARTITIONED)
    private String appPaymentCode;
    private String description;
    private Long period;
    @Column("speed_limit")
    private Long speedLimit;
    @Column("volume_limit")
    private Long volumeLimit;
    private Long price;

}
