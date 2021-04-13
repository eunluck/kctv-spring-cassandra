package com.kctv.api.model.interview;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@Table("interview_by_place_id")
public class InterviewByPlaceIdEntity {

    @PrimaryKeyColumn(value = "place_id",type = PrimaryKeyType.PARTITIONED)
    private UUID placeId;
    @Column("interview_id")
    private UUID interviewId;
    @Column("create_dt")
    private Date createDt;
}
