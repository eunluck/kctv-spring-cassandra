package com.kctv.api.repository.ap;

import com.kctv.api.entity.ap.WifiInfo;
import com.kctv.api.entity.user.UserInfo;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WifiRepository extends CassandraRepository<WifiInfo, String> {

    Optional<WifiInfo> findByApMac(String id);

    @AllowFiltering
    Optional<WifiInfo> findByPartnerId(UUID partnerId);

    @AllowFiltering
    List<WifiInfo> findByApLatGreaterThanAndApLatLessThanAndApLonGreaterThanAndApLonLessThan(Double maxLat, Double maxLon, Double minLat, Double minLon);




}
