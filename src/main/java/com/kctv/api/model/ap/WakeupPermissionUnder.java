package com.kctv.api.model.ap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Map<String,Long> device_mac_history;
    private Long expire_epoch;
    private Long volume;
    private String plan;
    private String unique_code;



}
