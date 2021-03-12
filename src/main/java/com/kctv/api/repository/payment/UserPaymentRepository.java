package com.kctv.api.repository.payment;

import com.kctv.api.model.payment.PaymentInfoEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.UUID;

public interface UserPaymentRepository extends CassandraRepository<PaymentInfoEntity, UUID> {

    List<PaymentInfoEntity> findByUserId(UUID userId); // 내 구매내역


}
