package com.kctv.api.repository.ad;

import com.kctv.api.model.admin.ad.CaptivePortalAdEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CaptivePortalAdRepository extends CassandraRepository<CaptivePortalAdEntity, UUID> {


    CaptivePortalAdEntity findByAdId(UUID uuid);



}
