package com.kctv.api.controller.v1;

import com.google.common.collect.Lists;
import com.kctv.api.entity.tag.StyleCardInfo;
import com.kctv.api.entity.tag.Tag;
import com.kctv.api.entity.user.UserInterestTag;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.StyleCardService;
import com.kctv.api.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.tinkerpop.gremlin.structure.T;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = {"04. StyleCardAPI"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class StyleCardContoller {

    private final StyleCardService styleCardService;
    private final UserService userService;
    private final ResponseService responseService;

    @ApiOperation(value = "계정에 등록된 태그를 통해 styleCard를 조회", notes = "태그에 충족하는 스타일카드를 검색한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @GetMapping("/card/tags/me")
    public ListResult<StyleCardInfo> getStyleCardMyList(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID uuid = UUID.fromString(authentication.getName());
        UserInterestTag resultUser = userService.getUserInterestTag(uuid);

        Set<String> tagList = resultUser.getTags();
        List<String> queryList = Lists.newArrayList();

        for (String tag:tagList) {
            queryList.add(tag);
        }

        return responseService.getListResult(styleCardService.getCardByTagsService(queryList));
    }

    /* tag를 통해 작성되어 있는 style Card 목록을 가져옴*/
    @ApiOperation(value = "태그를 통해 StyleCard를 검색.", notes = "태그에 충족하는 스타일카드를 검색한다.")
    @GetMapping("/card/tags/{tag}")
    public ListResult<StyleCardInfo> getStyleCardList(@ApiParam(value = "검색할 태그 입력(콤마','로 구분)",defaultValue = "제주여행,따뜻한")
                                                      @PathVariable("tag") String tags){

        List<String> tagArr = Arrays.asList(tags.split(","));

        return responseService.getListResult(styleCardService.getCardByTagsService(tagArr));

    }

    @ApiOperation(value = "card id로 특정 카드 상세보기", notes = "uuid를 통해 특정 카드를 검색한다.")
    @GetMapping("/card/{cardId}")
    public SingleResult<?> getCardById(@ApiParam(value = "검색할 Card UUID 입력",defaultValue = "fc35a91b-3bb2-4a55-8a45-3b03df9e797d")
                                       @PathVariable("cardId")UUID cardId){

        return responseService.getSingleResult(styleCardService.getCardById(cardId));

    }


    @ApiOperation(value = "LifeStyleCardList", notes = "등록된 모든 카드를 조회한다.")
    @GetMapping("/card")
    public ListResult<StyleCardInfo> getStyleCardAll(){

        return responseService.getListResult(styleCardService.getStyleCardListAllService());

    }




}
