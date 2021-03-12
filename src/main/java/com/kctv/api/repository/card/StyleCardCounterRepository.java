package com.kctv.api.repository.card;

import com.kctv.api.model.stylecard.StyleCardCounterEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface StyleCardCounterRepository extends CassandraRepository<StyleCardCounterEntity, UUID> {

    StyleCardCounterEntity findByCardId(UUID uuid);

    @Query("update style_card_counter set view_count = view_count + 1 where card_id=:cardId")
    void incrementViewCountByCardId(@Param("cardId") UUID uuid);

    @Query("update style_card_counter set scrap_count = scrap_count + 1 where card_id=:cardId")
    void incrementScrapCountByCardId(@Param("cardId")UUID uuid);

    @Query("update style_card_counter set scrap_count = scrap_count - 1 where card_id=:cardId")
    void decrementScrapCountByCardId(@Param("cardId")UUID uuid);


}
