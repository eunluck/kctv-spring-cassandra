package com.kctv.api.model.ap;

import com.google.common.collect.Sets;
import com.kctv.api.entity.user.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("wakeup_permission")
public class WakeupPermission {


    @PrimaryKeyColumn(value = "user_id",type = PrimaryKeyType.PARTITIONED)
    private UUID userId;
    @Column("device_mac")
    private Set<String> deviceMac;
    @Column("device_mac_history")
    private Map<String,Long> deviceMacHistory;
    @Column("expire_epoch")
    private Long expireEpoch;
    private Long volume;
    private String plan;
    @Column("unique_code")
    private String uniqueCode;

    public WakeupPermission(FindApRequest request,Date createDt){
        copyProperties(request,this);

    }

    public WakeupPermission (UserInfo userInfo,String uniqueCode){
        this.userId = userInfo.getUserId();
        this.plan = "무료";
        this.uniqueCode = uniqueCode;
    }


}
