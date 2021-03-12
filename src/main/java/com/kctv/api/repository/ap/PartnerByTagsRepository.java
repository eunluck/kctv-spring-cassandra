package com.kctv.api.repository.ap;

import com.kctv.api.model.stylecard.PartnersByTags;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.UUID;

public interface PartnerByTagsRepository extends CassandraRepository<PartnersByTags, UUID> {


    List<PartnersByTags> findByTagIn(List<String> tags);
    List<PartnersByTags> findByTag(String param);



}
