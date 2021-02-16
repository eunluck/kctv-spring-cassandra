package com.kctv.api.controller.v1;

import com.kctv.api.advice.exception.CCommunicationException;
import com.kctv.api.entity.stylecard.Tag;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.model.tag.Facilities;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.StyleCardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Api(tags = {"05. Tag API"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class TagController {


    private final StyleCardService styleCardService;
    private final ResponseService responseService;


    @ApiOperation(value = "태그 검색", notes = "태그 타입을 입력하여 소속된 태그들을 검색한다.")
    @GetMapping("/tags/{tagType}")
    public SingleResult<?> getListTags(@ApiParam(value = "태그타입으로 검색", defaultValue = "태그종류") @PathVariable("tagType") String tagType){


        return responseService.getSingleResult(styleCardService.getTagList(tagType));
    }

    @ApiOperation(value = "모든 태그 조회", notes = "등록된 모든 태그를 조회한다.")
    @GetMapping("/tags")
    public SingleResult<?> getListTagsAll(){


        return responseService.getSingleResult(styleCardService.getTagListAllService());
    }


    //tag가 중복되는지 단건 검사.
    @ApiOperation(value = "태그 중복 검사(true = 중복, false = 비중복)",notes = "이미 존재하는 태그인지 체크한다.")
    @GetMapping("/tags/{tagType}/{tagName}")
    public SingleResult<Boolean> tagCheck(@PathVariable("tagType")String tagType,
                                       @PathVariable("tagName") String tagName){

        Tag tag = Tag.builder().tagName(tagName).tagType(tagType).build();
        Optional<Tag> result = styleCardService.getTagOneService(tag);

        return responseService.getSingleResult(result.isPresent());


    }

    @ApiOperation(value = "편의시설 태그 목록 조회",notes = "장소를 추가할때 사용한다.")
    @GetMapping("/tags/facilities")
    public ListResult<String> getFacilities(){

        return responseService.getListResult(Facilities.facilities.getFacilitiesName());
    }


    @ApiOperation(value = "추천 키워드",notes = "무작위 키워드 5개를 조회한다.")
    @GetMapping("/tags/random")
    public ListResult<Tag> getRandomKeyword(){

        List<Tag> tagList = styleCardService.getTagListAllService().stream().filter(tag -> !"태그종류".equals(tag.getTagType())).collect(Collectors.toList());
        Collections.shuffle(tagList);

        return responseService.getListResult(tagList.stream().limit(5).collect(Collectors.toList()));
    }




}
