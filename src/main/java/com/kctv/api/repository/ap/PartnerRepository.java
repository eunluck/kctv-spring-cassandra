package com.kctv.api.repository.ap;

import com.kctv.api.entity.place.PlaceInfo;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartnerRepository extends CassandraRepository<PlaceInfo, UUID> {

    Optional<PlaceInfo> findByPartnerId(UUID id);

    @AllowFiltering
    List<PlaceInfo> findByBusinessNameContaining(String Param);

    List<PlaceInfo> findByPartnerIdIn(List<UUID> uuids);

}
