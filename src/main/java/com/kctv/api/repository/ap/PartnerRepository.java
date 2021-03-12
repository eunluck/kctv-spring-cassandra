package com.kctv.api.repository.ap;

import com.kctv.api.model.place.PlaceInfoEntity;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartnerRepository extends CassandraRepository<PlaceInfoEntity, UUID> {

    Optional<PlaceInfoEntity> findByPartnerId(UUID id);

    @AllowFiltering
    List<PlaceInfoEntity> findByBusinessNameContaining(String Param);

    List<PlaceInfoEntity> findByPartnerIdIn(List<UUID> uuids);

}
