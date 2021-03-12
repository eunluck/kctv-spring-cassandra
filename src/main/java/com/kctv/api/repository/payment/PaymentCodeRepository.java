package com.kctv.api.repository.payment;

import com.kctv.api.model.payment.PaymentCodeEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface PaymentCodeRepository extends CassandraRepository<PaymentCodeEntity, String> {

    PaymentCodeEntity findByAppPaymentCode(String code);


}
