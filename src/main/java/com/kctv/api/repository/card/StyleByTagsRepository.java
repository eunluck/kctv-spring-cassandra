package com.kctv.api.repository.card;

import com.kctv.api.entity.stylecard.StyleCardByTags;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

public interface StyleByTagsRepository extends CassandraRepository<StyleCardByTags, UUID> {


    List<StyleCardByTags> findByTagIn(List<String> tag);
    List<StyleCardByTags> findByTag(String tag);



}
