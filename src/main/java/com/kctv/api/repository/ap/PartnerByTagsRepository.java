package com.kctv.api.repository.ap;

import com.kctv.api.entity.ap.PartnerInfo;
import com.kctv.api.entity.tag.PartnersByTags;
import com.kctv.api.entity.tag.StyleCardByTags;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PartnerByTagsRepository extends CassandraRepository<PartnersByTags, UUID> {


    List<PartnersByTags> findByTagIn(List<String> tags);
    List<PartnersByTags> findByTag(String param);



}
