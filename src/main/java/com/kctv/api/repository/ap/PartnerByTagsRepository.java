package com.kctv.api.repository.ap;

import com.kctv.api.entity.stylecard.PartnersByTags;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PartnerByTagsRepository extends CassandraRepository<PartnersByTags, UUID> {


    List<PartnersByTags> findByTagIn(List<String> tags);
    List<PartnersByTags> findByTag(String param);



}
