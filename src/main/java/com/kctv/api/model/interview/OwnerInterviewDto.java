package com.kctv.api.model.interview;

import com.kctv.api.model.place.PlaceInfoEntity;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Data
public class OwnerInterviewDto {

    private UUID interviewId;
    private PlaceInfoEntity placeInfo;
    private String placeName;
    private String title;
    private String ownerSaying;
    private Date createDt;
    private Date modifyDt;
    private Boolean status;
    private Long cheeringCnt;
    private String dateText;
    private String storeType;
    private String coverImg;
    private List<InterviewContent> interviewContents;

    public OwnerInterviewDto(OwnerInterviewEntity ownerInterviewEntity, PlaceInfoEntity placeInfoEntity){

        this.coverImg = ownerInterviewEntity.getCoverImg();
        this.interviewId = ownerInterviewEntity.getInterviewId();
        this.placeInfo = placeInfoEntity;
        this.placeName = placeInfoEntity.getBusinessName();
        this.title = ownerInterviewEntity.getTitle();
        this.ownerSaying = ownerInterviewEntity.getOwnerSaying();
        this.createDt = ownerInterviewEntity.getCreateDt();
        this.modifyDt = ownerInterviewEntity.getModifyDt();
        this.status = ownerInterviewEntity.getStatus();
        this.storeType = placeInfoEntity.getStoreType();
        this.interviewContents = ownerInterviewEntity.getInterviewContents();
        this.dateText = new SimpleDateFormat("yyyy.MM.dd").format(ownerInterviewEntity.getCreateDt());
    }


    public OwnerInterviewDto dateFormatter(String pattern){
        this.setDateText(new SimpleDateFormat(pattern).format(this.getCreateDt()));
        return this;
    }
}
