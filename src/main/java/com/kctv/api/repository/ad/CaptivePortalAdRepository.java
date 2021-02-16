package com.kctv.api.repository.ad;

import com.kctv.api.entity.admin.ad.CaptivePortalAdEntity;
import com.kctv.api.entity.place.WifiInfo;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaptivePortalAdRepository extends CassandraRepository<CaptivePortalAdEntity, UUID> {


    CaptivePortalAdEntity findByAdId(UUID uuid);



}
