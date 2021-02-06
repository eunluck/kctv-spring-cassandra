package com.kctv.api.model.visit;

import com.kctv.api.entity.place.PlaceInfo;
import com.kctv.api.entity.place.PlaceInfoDto;
import com.kctv.api.entity.visit.UserVisitHistoryEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UserVisitHistoryDto {

    private UUID userId;
    private int visitDate;
    private PlaceInfo placeInfo;
    private Date timestamp;
    private String userMac;

    public UserVisitHistoryDto(UserVisitHistoryEntity userVisitHistoryEntity,Long timestamp, PlaceInfo placeInfo){
        BeanUtils.copyProperties(userVisitHistoryEntity,this);
        this.timestamp = new Date(timestamp * 1000L);
        this.placeInfo = placeInfo;
    }
    public UserVisitHistoryDto(UUID userId){
        this.userId = userId;
    }
}
