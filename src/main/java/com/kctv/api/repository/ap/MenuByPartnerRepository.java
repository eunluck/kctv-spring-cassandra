package com.kctv.api.repository.ap;


import com.kctv.api.model.place.MenuByPlaceEntity;

import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

import java.util.UUID;

public interface MenuByPartnerRepository extends CassandraRepository<MenuByPlaceEntity, UUID> {

    List<MenuByPlaceEntity> findByPartnerId(UUID id);

}
