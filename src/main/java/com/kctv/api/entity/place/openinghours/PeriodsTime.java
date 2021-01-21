
package com.kctv.api.entity.place.openinghours;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@UserDefinedType(value = "periods_time")
public class PeriodsTime {

    private int day;
    private String time;
}
