package com.kctv.api.controller.v1;

import com.kctv.api.advice.exception.CCommunicationException;
import com.kctv.api.entity.tag.Tag;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.StyleCardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


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

    @ApiOperation(value = "신규 태그 추가",notes = "신규 태그를 추가한다.(관리자용)")
    @PostMapping("/tags")
    public CommonResult createTags(List<Tag> tags){

        for (Tag tag : tags){
            styleCardService.createTagService(tag).orElseThrow(CCommunicationException::new);
        }

        return responseService.getSuccessResult();
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

}
