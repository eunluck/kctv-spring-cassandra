package com.kctv.api.model.ap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kctv.api.model.user.UserInfoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.beans.BeanUtils.copyProperties;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("wakeup_permission")
public class WakeupPermissionEntity {


    @PrimaryKeyColumn(value = "user_id",type = PrimaryKeyType.PARTITIONED)
    private UUID userId;
    @Column("device_mac")
    private Set<String> deviceMac;
    @Column("device_mac_history")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Map<String,Long> deviceMacHistory;
    @Column("expire_epoch")
    private Long expireEpoch;
    private String plan;
    @ReadOnlyProperty
    private String planText;
    @Column("unique_code")
    private String uniqueCode;
    private Long volume;
    @Column("use_volume")
    private Long useVolume;
    @ReadOnlyProperty
    private String volumeText;



    public WakeupPermissionEntity(FindApRequest request, Date createDt){
        copyProperties(request,this);
    }

    public WakeupPermissionEntity(UserInfoEntity userInfoEntity, String uniqueCode){
        this.userId = userInfoEntity.getUserId();
        this.plan = "FREE";
        this.uniqueCode = uniqueCode;
    }

    public void subscribeUser(Long expireEpoch,String planCode,Long volume){
        this.deviceMac = null;
        this.expireEpoch = (System.currentTimeMillis()) + expireEpoch;
        this.plan = planCode;
        this.useVolume = 0L;
        this.volume = volume;
    }

    public void expirationUser(){
        this.deviceMac = null;
        this.plan = "FREE";
        this.volume = 524288000L;
        this.useVolume = null;
        this.expireEpoch = getmidnight();

    }

    public long getmidnight() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String aaa = sdf.format(new Date(System.currentTimeMillis()));
        long ts = 0;
        try {
            Date d = sdf.parse(aaa);
            ts = d.getTime() + 3600 * 24 * 1000;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return ts;
    } //자정 가져오기





}
