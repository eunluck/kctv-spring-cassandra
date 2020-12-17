package com.kctv.api.entity.partner.openinghours;

import lombok.Data;
import lombok.Value;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@UserDefinedType(value = "close_or_open")
@Data
public class CloseOrOpen {

    @CassandraType(type = CassandraType.Name.UDT,userTypeName = "periods_time")
    private PeriodsTime open;
    @CassandraType(type = CassandraType.Name.UDT,userTypeName = "periods_time")
    private PeriodsTime close;


    @UserDefinedType(value = "periods_time")
    @Data
    public static class PeriodsTime {

        @CassandraType(type = CassandraType.Name.INT)
        private int day;
        private String time;
    }

}
