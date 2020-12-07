package com.kctv.api.repository.card;

import com.kctv.api.entity.tag.StyleCardByTags;
import com.kctv.api.entity.tag.StyleCardInfo;
import org.apache.tinkerpop.gremlin.structure.T;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StyleByTagsRepository extends CassandraRepository<StyleCardByTags, UUID> {


    List<StyleCardByTags> findByTagIn(List<String> tag);
    List<StyleCardByTags> findByTag(String tag);



}
