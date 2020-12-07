package com.kctv.api.controller.v1;

import com.google.common.collect.Lists;
import com.kctv.api.entity.ap.PartnerInfo;
import com.kctv.api.entity.tag.StyleCardInfo;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.SearchService;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class SerachContorller {

    private final ResponseService responseService;
    private final SearchService searchService;
    //set으로 저장한 태그에서 세컨더리인덱스가 안먹는다.
    //

    //검색어를 입력받으면
    //partnerInfo, styleCardInfo 두곳을 두번씩 검색한다.
    //partnerInfo where name = ,
    //partnerBytags where tags = ,
    //위 두개를 합치고 중복을 제거한다.

    //stylecardInfo where tag =
    //stylecardInfo where name =
    //위 두개도 합치고 중복을 제거한다.

    @GetMapping("/search/{param}/place")
    public ListResult<PartnerInfo> serachPlace(@ApiParam("검색어") @PathVariable("param")String param){



        return responseService.getListResult(searchService.searchPartner(param));
    }

    @GetMapping("/search/{param}/card")
    public ListResult<StyleCardInfo> serachCard(@ApiParam("검색어") @PathVariable("param")String param){



        return responseService.getListResult(searchService.searchCard(param));
    }

}
