package com.kctv.api.entity.ap;


import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table("visit_history")
public class VisitHistoryEntity {

    @PrimaryKeyColumn(value = "bssid", type = PrimaryKeyType.PARTITIONED, ordinal = 0)
    private String bssid;
    @PrimaryKeyColumn(value = "framed_ip_address",type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    private String framedIpAddress;
    @PrimaryKeyColumn(value = "aggr_day",type = PrimaryKeyType.CLUSTERED,ordinal = 2)
    private String aggrDay;
    @Column("ap_mac")
    private String apMac;
    @Column("calledstation_id")
    private String calledstationId;
    @Column("callingstation_id")
    private String callingstationId;
    @Column("client_mac")
    private String clientMac;
    private String ssid;
    @Column("visit_date")
    private String visitDate;

}
