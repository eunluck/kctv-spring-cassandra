package com.kctv.api.repository.ap;

import com.kctv.api.model.place.WifiInfoEntity;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface WifiRepository extends CassandraRepository<WifiInfoEntity, String> {

    Optional<WifiInfoEntity> findByApMac(String id);

    @AllowFiltering
    Optional<WifiInfoEntity> findByPartnerId(UUID partnerId);

    @AllowFiltering
    List<WifiInfoEntity> findByApLatGreaterThanAndApLatLessThanAndApLonGreaterThanAndApLonLessThan(Double maxLat, Double maxLon, Double minLat, Double minLon);




}
