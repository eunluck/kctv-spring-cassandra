package com.kctv.api.model.interview;

import com.kctv.api.model.place.PlaceInfoEntity;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Data
@Table("owner_interview")
public class OwnerInterviewEntity {

    @PrimaryKeyColumn(value = "interview_id",type = PrimaryKeyType.PARTITIONED,ordinal = 0)
    private UUID interviewId;
    @Column("place_id")
    private UUID placeId;
    private String title;
    @Column("cover_image")
    private String coverImg;
    @Column("owner_saying")
    private String ownerSaying;
    //private PlaceInfoEntity placeInfo;
    @PrimaryKeyColumn(value = "create_dt",type = PrimaryKeyType.CLUSTERED,ordering = Ordering.DESCENDING,ordinal = 1)
    private Date createDt;
    @Column("modify_dt")
    private Date modifyDt;
    @CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.UDT, userTypeName = "interview_content")
    @Column("interview_contents")
    private List<InterviewContent> interviewContents;
    private Boolean status;

}
