package com.kctv.api.model.ap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.springframework.beans.BeanUtils.copyProperties;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WakeupPermissionVO {


    private WakeupPermissionUnder recvUser;
    private WakeupPermissionUnder sendUser;

}
