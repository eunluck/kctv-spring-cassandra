package com.kctv.api.entity.place.openinghours;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Frozen;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@Data
@Getter
@Setter
@UserDefinedType(value = "close_or_open")
public class CloseOrOpen {

    @Frozen
    /*@CassandraType(type = CassandraType.Name.UDT,userTypeName = "periods_time")*/
    private PeriodsTime open;
    @Frozen
    /*@CassandraType(type = CassandraType.Name.UDT,userTypeName = "periods_time")*/
    private PeriodsTime close;


    @UserDefinedType(value = "periods_time")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodsTime {

        private int day;
        private String time;
    }

}
