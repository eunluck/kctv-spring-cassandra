package com.kctv.api.repository.card;

import com.kctv.api.model.stylecard.StyleCardInfoEntity;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StyleCardRepository extends CassandraRepository<StyleCardInfoEntity, UUID> {


    List<StyleCardInfoEntity> findByCardIdIn(List<UUID> cardId);

    Optional<StyleCardInfoEntity> findByCardId(UUID cardId);

    @AllowFiltering
    List<StyleCardInfoEntity> findByTitleContaining(String param);


}
