package com.kctv.api.service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.session.Session;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.shaded.guava.common.collect.Lists;
import com.kctv.api.entity.visit.UserVisitHistoryEntity;
import com.kctv.api.entity.visit.VisitCount;
import com.kctv.api.repository.visit.VisitHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserVisitHistoryService {


    private final CassandraTemplate cassandraTemplate;
    private final VisitHistoryRepository visitHistoryRepository;


    public List<UserVisitHistoryEntity> findHistoryByUserId(UUID userId){


        List<UserVisitHistoryEntity> user = visitHistoryRepository.findFirst20ByUserId(userId).stream().sorted(Comparator.comparingLong(UserVisitHistoryEntity::getTimestamp)).collect(Collectors.toList());

        return user;
    }



    public List<VisitCount> visitCounts(UUID userId){
//        Select select = QueryBuilder.selectFrom("kctv", "place_user_visit_history_view").all().whereColumn("user_id").
        CqlTemplate cqlTemplate = (CqlTemplate) cassandraTemplate.getCqlOperations();
        CqlSession session = cqlTemplate.getSession();

        ResultSet resultSet = session.execute("SELECT place_id, count(*) as cnt FROM kctv.place_user_visit_history_view WHERE user_id="+userId.toString()+" GROUP BY place_id");

        List list = Lists.newArrayList();
        for(Row row:resultSet) {
            UUID placeId = row.getUuid("place_id");
            Long visitCount = row.getLong("cnt");

           list.add(new VisitCount(userId,placeId,visitCount));

        }


//        List<VisitCount> list = cassandraTemplate.select("SELECT place_id, count(*) FROM kctv.place_user_visit_history_view WHERE user_id=39736254-55eb-4652-b69c-7092ddc22f21 GROUP BY place_id",VisitCount.class);

        return list;
    }

}
