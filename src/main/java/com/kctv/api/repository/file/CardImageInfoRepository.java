package com.kctv.api.repository.file;

import com.kctv.api.entity.stylecard.CardImageInfo;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;



public interface CardImageInfoRepository extends CassandraRepository<CardImageInfo, UUID> {


    Optional<CardImageInfo> findByImageId(UUID uuid);






}
