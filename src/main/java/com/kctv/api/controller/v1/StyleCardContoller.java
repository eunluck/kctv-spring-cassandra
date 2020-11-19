package com.kctv.api.controller.v1;

import com.kctv.api.entity.tag.StyleCardInfo;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.StyleCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class StyleCardContoller {

    private final StyleCardService styleCardService;
    private final ResponseService responseService;


    
    /* tag를 통해 작성되어 있는 style Card 목록을 가져옴*/
    @GetMapping("/tags/style/{tag}")
    public List<StyleCardInfo> getStyleCardList(@PathVariable("tag") String tags){

        List<String> tagArr = Arrays.asList(tags.split(","));

        return styleCardService.getCardByTagsService(tagArr);


    }

    /* 현재 태그목록 조회*/
    @GetMapping("/tags/{tagName}/{tagType}")
    public SingleResult<?> getListTags(@PathVariable("tagType") String tagType, @PathVariable("tagName") String tagName){


        return responseService.getSingleResult(styleCardService.getTagList(tagType));
    }



}
