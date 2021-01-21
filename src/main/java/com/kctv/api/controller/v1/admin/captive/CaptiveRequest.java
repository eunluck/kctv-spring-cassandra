package com.kctv.api.controller.v1.admin.captive;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CaptiveRequest {

    private String captiveLink;
    private MultipartFile imgFile;
    private String startDate;
    private String endDate;
    private String status;

}
