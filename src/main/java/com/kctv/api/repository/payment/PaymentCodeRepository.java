package com.kctv.api.repository.payment;

import com.kctv.api.entity.payment.PaymentCode;
import com.kctv.api.entity.payment.PaymentInfo;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentCodeRepository extends CassandraRepository<PaymentCode, String> {

    PaymentCode findByAppPaymentCode(String code);


}
