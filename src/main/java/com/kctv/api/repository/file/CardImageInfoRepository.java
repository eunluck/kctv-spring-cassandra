package com.kctv.api.repository.file;

import com.kctv.api.model.stylecard.CardImageInfoEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.Optional;
import java.util.UUID;



public interface CardImageInfoRepository extends CassandraRepository<CardImageInfoEntity, UUID> {


    Optional<CardImageInfoEntity> findByImageId(UUID uuid);






}
