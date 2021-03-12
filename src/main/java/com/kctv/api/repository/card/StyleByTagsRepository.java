package com.kctv.api.repository.card;

import com.kctv.api.model.stylecard.StyleCardByTags;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.UUID;

public interface StyleByTagsRepository extends CassandraRepository<StyleCardByTags, UUID> {


    List<StyleCardByTags> findByTagIn(List<String> tag);
    List<StyleCardByTags> findByTag(String tag);



}
