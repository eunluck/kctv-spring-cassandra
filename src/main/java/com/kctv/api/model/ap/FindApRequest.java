package com.kctv.api.model.ap;


import lombok.Data;

import java.util.UUID;

@Data
public class FindApRequest {

    private UUID userId;
    private String createDt;
    private String deviceMac;
    private String plan;

}
