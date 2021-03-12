package com.kctv.api.repository.tag;

import com.kctv.api.model.tag.TagEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TagRepository extends CassandraRepository<TagEntity,String> {


    List<TagEntity> findByTagType(String tagType);


    Optional<TagEntity> findByTagTypeAndTagName(String tagType, String tagName);





}
