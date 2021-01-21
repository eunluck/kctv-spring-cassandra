package com.kctv.api.repository.card;

import com.kctv.api.entity.stylecard.StyleCardInfo;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface StyleCardRepository extends CassandraRepository<StyleCardInfo, UUID> {


    List<StyleCardInfo> findByCardIdIn(List<UUID> cardId);

    Optional<StyleCardInfo> findByCardId(UUID cardId);

    @AllowFiltering
    List<StyleCardInfo> findByTitleContaining(String param);


}
