package com.kctv.api.repository.file;

import com.kctv.api.entity.tag.Tag;
import com.kctv.api.model.CardImageInfo;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface CardImageInfoRepository extends CassandraRepository<CardImageInfo, UUID> {


    Optional<CardImageInfo> findByImageId(UUID uuid);






}
