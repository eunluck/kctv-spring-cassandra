package com.kctv.api.controller.v1;

import com.kctv.api.entity.tag.StyleCardInfo;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.StyleCardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Api(tags = {"03. StyleCard And Tag API"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class StyleCardContoller {

    private final StyleCardService styleCardService;
    private final ResponseService responseService;


    
    /* tag를 통해 작성되어 있는 style Card 목록을 가져옴*/
    @ApiOperation(value = "태그를 통해 StyleCard를 검색.", notes = "태그에 충족하는 스타일카드를 검색한다.")
    @GetMapping("/card/tags/{tag}")
    public List<StyleCardInfo> getStyleCardList(@ApiParam(value = "검색할 태그 입력(콤마','로 구분)",defaultValue = "제주여행,따뜻한")@PathVariable("tag") String tags){

        List<String> tagArr = Arrays.asList(tags.split(","));

        return styleCardService.getCardByTagsService(tagArr);

    }

    @ApiOperation(value = "card id로 특정 카드 검색", notes = "uuid를 통해 특정 카드를 검색한다.")
    @GetMapping("/card/{cardId}")
    public SingleResult<?> getCardById(@ApiParam(value = "검색할 Card UUID 입력",defaultValue = "fc35a91b-3bb2-4a55-8a45-3b03df9e797d")@PathVariable("cardId")UUID cardId){

        return responseService.getSingleResult(styleCardService.getCardById(cardId));

    }


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

}
