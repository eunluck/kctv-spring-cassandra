package com.kctv.api.repository.ad;

import com.kctv.api.entity.admin.ad.CaptivePortalAdEntity;
import com.kctv.api.entity.place.WifiInfo;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CaptivePortalAdRepository extends CassandraRepository<CaptivePortalAdEntity, UUID> {


    CaptivePortalAdEntity findByAdId(UUID uuid);

    void deleteByAdId(UUID uuid);



}
