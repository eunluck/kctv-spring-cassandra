package com.kctv.api.controller.v1;


import com.kctv.api.model.admin.FaqTableEntity;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.FaqService;
import com.kctv.api.service.ResponseService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Api(tags = {"08. Faq API"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class FaqController {

    private final FaqService faqService;
    private final ResponseService responseService;

    @GetMapping("/faq")
    public ListResult<FaqTableEntity> findByAllFaq(){

        return responseService.getListResult(faqService.findByAll());

    }

    @GetMapping("/faq/{id}")
    public SingleResult<FaqTableEntity> findById(@PathVariable("id")UUID uuid){

        return responseService.getSingleResult(faqService.findById(uuid));
    }

}
