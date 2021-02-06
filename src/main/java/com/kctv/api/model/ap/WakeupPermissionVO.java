package com.kctv.api.model.ap;

import com.kctv.api.entity.user.UserInfo;
import com.kctv.api.model.request.ReferRequest;
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
public class WakeupPermissionVO {


    private WakeupPermissionUnder recvUser;
    private WakeupPermissionUnder sendUser;

}
