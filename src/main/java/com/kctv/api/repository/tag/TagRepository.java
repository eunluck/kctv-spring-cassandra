package com.kctv.api.repository.tag;

import com.kctv.api.entity.stylecard.Tag;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TagRepository extends CassandraRepository<Tag,String> {


    List<Tag> findByTagType(String tagType);


    Optional<Tag> findByTagTypeAndTagName(String tagType,String tagName);





}
