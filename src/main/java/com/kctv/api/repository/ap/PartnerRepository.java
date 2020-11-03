package com.kctv.api.repository.ap;

import com.kctv.api.entity.ap.PartnerInfo;
import com.kctv.api.entity.ap.WifiInfo;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartnerRepository extends CassandraRepository<PartnerInfo, UUID> {

    Optional<PartnerInfo> findByPartnerId(UUID id);

    @Override
    Slice<PartnerInfo> findAll(Pageable pageable);

}
