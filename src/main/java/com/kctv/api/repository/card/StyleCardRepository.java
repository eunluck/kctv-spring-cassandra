package com.kctv.api.repository.card;

import com.kctv.api.entity.tag.StyleCardByTags;
import com.kctv.api.entity.tag.StyleCardInfo;
import com.kctv.api.entity.tag.Tag;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


public interface StyleCardRepository extends CassandraRepository<StyleCardInfo, UUID> {


    List<StyleCardInfo> findByCardIdIn(List<UUID> cardId);

    Optional<StyleCardInfo> findByCardId(UUID cardId);

    @AllowFiltering
    List<StyleCardInfo> findByTitleContaining(String param);
}
