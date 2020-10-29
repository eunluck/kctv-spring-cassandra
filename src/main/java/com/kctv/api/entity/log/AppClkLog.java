package com.kctv.api.entity.log;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tinkerpop.shaded.jackson.annotation.JsonFormat;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "app_clk_log")
public class AppClkLog {

    @PrimaryKeyColumn(value = "user_id",type = PrimaryKeyType.PARTITIONED,ordinal = 0)
    @ApiModelProperty(value = "가입 당시 생성된 고유 ID",readOnly = true, example = "4df4e39e-0f50-409f-9e81-14bf37189706")
    private UUID userId;

    @ApiModelProperty(value = "이벤트 발생 월",dataType = "Date", example = "202010")
    private int month;

    @ApiModelProperty(value = "클릭 시간",dataType = "String", example = "2020-10-12T17:36:33")
    @Column("clk_ts")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date clkTs;

    @ApiModelProperty(value = "이벤트 발생 영역",dataType = "String", example = "1")
    @Column("clk_res")
    private String clkRes;

    @ApiModelProperty(value = "batch로 일시",dataType = "String", example = "2020-10-13T00:00:00")
    @Column("batch_dt")
    private Date batchDt;


}
