package com.kctv.api.controller.v1.admin.captive;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CaptiveAdEntity {

    private String captiveLink;
    private String img;
    private String startDate;
    private String endDate;
    private String status;

}
