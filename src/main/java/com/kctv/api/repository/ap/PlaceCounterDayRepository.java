package com.kctv.api.repository.ap;

import com.kctv.api.model.place.PlaceCounterByDayEntity;
import com.kctv.api.model.stylecard.StyleCardCounterByDayEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PlaceCounterDayRepository extends CassandraRepository<PlaceCounterByDayEntity, PlaceCounterByDayEntity.PlaceCounterKey> {


    @Query("select * FROM kctv_dev.place_counter_by_day where update_day >= :week ALLOW FILTERING")
    List<PlaceCounterByDayEntity> findByWeekCount(@Param("week") Long week);

    @Query("update place_counter_by_day set view_count = view_count + 1 where update_day=:week AND place_id=:placeId")
    void incrementViewCountByPlaceId(@Param("week")Long week, @Param("placeId") UUID uuid);

    @Query("update place_counter_by_day set like_count = like_count + 1 where update_day=:week AND place_id=:placeId")
    void incrementLikeCountByPlaceId(@Param("week") Long week,@Param("placeId")UUID uuid);

    @Query("update place_counter_by_day set like_count = like_count - 1 where update_day=:week AND place_id=:placeId")
    void decrementLikeCountByPlaceId(@Param("week") Long week,@Param("placeId")UUID uuid);


}
