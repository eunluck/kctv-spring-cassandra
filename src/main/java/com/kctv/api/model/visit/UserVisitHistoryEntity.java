package com.kctv.api.model.visit;


import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.kctv.api.model.qna.QnaRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

import static org.springframework.beans.BeanUtils.copyProperties;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table("user_visit_history")
public class UserVisitHistoryEntity {

    @PrimaryKeyColumn(value = "user_id",type = PrimaryKeyType.PARTITIONED,ordinal = 0)
    private UUID userId;
    @PrimaryKeyColumn(value = "visit_date",type = PrimaryKeyType.CLUSTERED,ordering = Ordering.DESCENDING, ordinal = 1)
    private int visitDate;
    @PrimaryKeyColumn(value = "place_id",type = PrimaryKeyType.CLUSTERED, ordinal = 2)
    private UUID placeId;
    private Long timestamp;
    private String userMac;


}
