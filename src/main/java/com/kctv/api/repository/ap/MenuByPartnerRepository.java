package com.kctv.api.repository.ap;


import com.kctv.api.entity.place.MenuByPlace;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.UUID;

public interface MenuByPartnerRepository extends CassandraRepository<MenuByPlace, UUID> {

    List<MenuByPlace> findByPartnerId(UUID id);

}
