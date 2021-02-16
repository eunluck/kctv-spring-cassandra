package com.kctv.api.repository.card;

import com.kctv.api.entity.stylecard.StyleCardCounter;
import com.kctv.api.entity.stylecard.StyleCardCounterByDay;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

public interface StyleCardCounterDayRepository extends CassandraRepository<StyleCardCounterByDay, StyleCardCounterByDay.StyleCardCounterKey> {


    @Query("select * FROM kctv_dev.style_card_counter_by_day where update_day >= :week ALLOW FILTERING")
    List<StyleCardCounterByDay> findByWeekCount(@Param("week") Long week);

    @Query("update style_card_counter_by_day set view_count = view_count + 1 where update_day=:week AND card_id=:cardId")
    void incrementViewCountByCardId(@Param("week")Long week, @Param("cardId") UUID uuid);

    @Query("update style_card_counter_by_day set scrap_count = scrap_count + 1 where update_day=:week AND card_id=:cardId")
    void incrementScrapCountByCardId(@Param("week") Long week,@Param("cardId")UUID uuid);

    @Query("update style_card_counter_by_day set scrap_count = scrap_count - 1 where update_day=:week AND card_id=:cardId")
    void decrementScrapCountByCardId(@Param("week") Long week,@Param("cardId")UUID uuid);


}
