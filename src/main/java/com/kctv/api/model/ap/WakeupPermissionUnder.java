package com.kctv.api.model.ap;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class WakeupPermissionUnder {


    private UUID user_id;
    private Set<String> device_mac;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Map<String,Long> device_mac_history;
    private Long expire_epoch;
    private Long volume;
    private String plan;
    private String unique_code;



}
