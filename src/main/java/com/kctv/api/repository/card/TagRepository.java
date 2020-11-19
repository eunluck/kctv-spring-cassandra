package com.kctv.api.repository.card;

import com.kctv.api.entity.tag.Tag;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TagRepository extends CassandraRepository<Tag,String> {


    List<Tag> findByTagType(String tagType);





}
