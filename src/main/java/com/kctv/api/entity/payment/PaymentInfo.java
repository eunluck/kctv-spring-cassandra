package com.kctv.api.entity.payment;

import com.kctv.api.model.request.SubscribeRequest;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

@Data
@Table("user_payment_info")
public class PaymentInfo {

    @PrimaryKeyColumn(value = "user_id",type = PrimaryKeyType.PARTITIONED,ordinal = 0)
    private UUID userId;
    @PrimaryKeyColumn(value = "create_dt",type = PrimaryKeyType.CLUSTERED,ordering = Ordering.DESCENDING,ordinal = 1)
    private Date createDt;
    @Column("app_payment_code")
    private String appPaymentCode;
    @Column("product_id")
    private String productId;
    private String currency;
    @Column("country_code")
    private String countryCode;
    @Column("localized_price")
    private String localizedPrice;
    private String title;
    private String description;
    @Column("introductory_price")
    private String introductoryPrice;
    @Column("introductory_price_as_amount_ios")
    private String introductoryPriceAsAmountIos;
    @Column("introductory_price_payment_mode_ios")
    private String introductoryPricePaymentModeIos;
    @Column("introductory_price_number_of_periods")
    private String introductoryPriceNumberOfPeriods;
    @Column("introductory_price_number_of_periods_ios")
    private String introductoryPriceNumberOfPeriodsIos;
    @Column("introductory_price_subscription_period")
    private String introductoryPriceSubscriptionPeriod;
    @Column("introductory_price_subscription_period_ios")
    private String introductoryPriceSubscriptionPeriodIos;
    @Column("subscription_period_number_ios")
    private String subscriptionPeriodNumberIos;
    @Column("subscription_period_unit_ios")
    private String subscriptionPeriodUnitIos;
    @Column("subscription_period_android")
    private String subscriptionPeriodAndroid;
    @Column("introductory_price_cycles_android")
    private String introductoryPriceCyclesAndroid;
    @Column("introductory_price_period_android")
    private String introductoryPricePeriodAndroid;
    @Column("free_trial_period_android")
    private String freeTrialPeriodAndroid;


    public PaymentInfo(UUID userId, String appPaymentCode, Date createDt){
       this.userId = userId;
       this.appPaymentCode = appPaymentCode;
       this.createDt = createDt;
    }

}
