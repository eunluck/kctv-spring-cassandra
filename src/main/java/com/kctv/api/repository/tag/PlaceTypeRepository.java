package com.kctv.api.repository.tag;

import com.kctv.api.entity.place.PlaceTypeEntity;
import com.kctv.api.entity.stylecard.Tag;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface PlaceTypeRepository extends CassandraRepository<PlaceTypeEntity,String> {


    List<PlaceTypeEntity> findByPlaceParentType(String placeType);


    Optional<PlaceTypeEntity> findByPlaceParentTypeAndPlaceType(String placeParentType,String placeType);





}
