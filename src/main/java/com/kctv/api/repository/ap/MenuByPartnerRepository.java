package com.kctv.api.repository.ap;


import com.kctv.api.entity.partner.MenuByPlace;

import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

import java.util.UUID;

public interface MenuByPartnerRepository extends CassandraRepository<MenuByPlace, UUID> {

    List<MenuByPlace> findByPartnerId(UUID id);

}
