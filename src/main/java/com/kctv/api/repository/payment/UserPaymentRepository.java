package com.kctv.api.repository.payment;

import com.kctv.api.entity.payment.PaymentInfo;
import com.kctv.api.entity.user.UserLikePartner;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserPaymentRepository extends CassandraRepository<PaymentInfo, UUID> {

    List<PaymentInfo> findByUserId(UUID userId); // 내 구매내역


}
