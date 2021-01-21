package com.kctv.api.model.ap;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.springframework.beans.BeanUtils.copyProperties;


@Data
@Table("wakeup_permission")
public class WakeupPermission {


    @PrimaryKeyColumn(value = "user_id",type = PrimaryKeyType.PARTITIONED)
    private UUID userId;
    @Column("device_mac")
    private Set<String> deviceMac;
    @Column("create_dt")
    private Map<String,Long> deviceMacHistory;
    @Column("create_dt")
    private Date createDt;
    @Column("modify_dt")
    private Date modifyDt;
    @Column("expire_epoch")
    private Long expireEpoch;

    private Long volume;
    private String plan;

    public WakeupPermission(FindApRequest request,Date createDt){
        copyProperties(request,this);



    }


}
