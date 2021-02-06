package com.kctv.api.controller.v1;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.entity.place.PlaceInfo;
import com.kctv.api.entity.stylecard.StyleCardCounter;
import com.kctv.api.entity.stylecard.StyleCardInfo;

import com.kctv.api.entity.user.UserInfoDto;
import com.kctv.api.entity.user.UserInterestTag;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.model.response.PlaceListResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.PlaceService;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.StyleCardService;
import com.kctv.api.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;


import java.util.*;
import java.util.stream.Collectors;

@Api(tags = {"04. StyleCardAPI"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class StyleCardController {

    private final PlaceService placeService;
    private final StyleCardService styleCardService;
    private final UserService userService;
    private final ResponseService responseService;

    @ApiOperation(value = "계정에 등록된 태그를 통해 styleCard를 조회", notes = "태그에 충족하는 스타일카드를 검색한다. 태그를 등록하지 않았을 시 랜덤으로 생성된다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @GetMapping("/card/me")
    public ListResult<StyleCardInfo> getStyleCardMyList(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID uuid = UUID.fromString(authentication.getName());

        UserInterestTag connectionUser = userService.getUserInterestTag(uuid).orElseGet(() -> new UserInterestTag(uuid,null,Sets.newHashSet()));
        UserInfoDto userInfoDto = new UserInfoDto(userService.findByUserId(uuid),new ArrayList<>(connectionUser.getTags()));
        String filterAge = Optional.ofNullable(userInfoDto.getAges()).orElseGet(() -> "20대");
        String filterGender = Optional.ofNullable(userInfoDto.getUserGender()).orElseGet(() -> "남");


        //UserInfoDto userDto = new UserInfoDto(userService.findByUserId(uuid), new ArrayList<>(connectionUser.getTags()));

        if (!CollectionUtils.isEmpty(connectionUser.getTags())){

            return responseService.getListResult(styleCardService.getCardByTagsService(new ArrayList<>(connectionUser.getTags()))
                    .stream()
                    .filter(styleCardInfo -> !CollectionUtils.isEmpty(styleCardInfo.getAges()) && styleCardInfo.getAges().contains(filterAge))
                    .filter(styleCardInfo -> !CollectionUtils.isEmpty(styleCardInfo.getGender()) && styleCardInfo.getGender().contains(filterGender))
                    .limit(10L)
                    .collect(Collectors.toList()));
        }else {
            List<StyleCardInfo> randomList = styleCardService.getStyleCardListAllService().stream()
                    .limit(10L)
                    .collect(Collectors.toList());
            Collections.shuffle(randomList);
            return responseService.getListResult(randomList);
        }
    }

    @ApiOperation(value = "관련된 스타일카드", notes = "해당 카드와 관련된 스타일카드 리스트 조회한다")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @GetMapping("/card/{cardId}/relevant")
    public ListResult<StyleCardInfo> getStyleCardRelevant(@PathVariable("cardId") UUID cardId){
        
        //TODO 정직하게 구현 필요
        List<StyleCardInfo> randomList = styleCardService.getStyleCardListAllService().stream().limit(5).collect(Collectors.toList());
        Collections.shuffle(randomList);
        return responseService.getListResult(randomList);
        
    }

    /* tag를 통해 작성되어 있는 style Card 목록을 가져옴*/
    @ApiOperation(value = "태그를 통해 StyleCard를 검색.", notes = "태그에 충족하는 스타일카드를 검색한다.")
    @GetMapping("/card/tags/{tag}")
    public ListResult<StyleCardInfo> getStyleCardList(@ApiParam(value = "검색할 태그 입력(콤마','로 구분)",defaultValue = "제주여행,따뜻한")
                                                      @PathVariable("tag") String tags){

        List<String> tagArr = Arrays.asList(tags.split(","));
        if (tagArr.size() > 0){
        return responseService.getListResult(styleCardService.getCardByTagsService(tagArr));

        }else {
            return responseService.getListResult(Lists.newArrayList());
        }


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


    @ApiOperation(value = "card id로 가게리스트 검색", notes = "card uuid를 통해 태그에 충족되는 가게 리스트를 조회한다.")
    @GetMapping("/card/{cardId}/place")
    public PlaceListResult getPlaceByTags(@ApiParam(value = "검색할 Card UUID 입력",defaultValue = "fc35a91b-3bb2-4a55-8a45-3b03df9e797d")
                                          @PathVariable("cardId")UUID cardId,
                                          @RequestParam(required = false,value = "type",defaultValue = "id")String type){

        //타입을 입력받게되면 tag기반으로 검색된 플레이스 리스트가 나타난다.


        StyleCardInfo cardInfo = styleCardService.getCardById(cardId);
        if (CollectionUtils.isEmpty(cardInfo.getPlaceId())){
            type = "tag";
        }

        List<PlaceInfo> placeInfoList = Lists.newArrayList();


        if("tag".equals(type)) {
            placeInfoList.addAll(placeService.getPartnerInfoListByTagsService(Lists.newArrayList(cardInfo.getTags())));
        }else if("id".equals(type)){
            placeInfoList.addAll(placeService.getPlaceListByIdIn(Lists.newArrayList(cardInfo.getPlaceId())));
        }



        return responseService.getPlaceListResult(cardInfo, placeInfoList);

    }

    @ApiOperation(value = "스타일카드 조회수 랭킹 TOP5", notes = "가장 많이 조회된 스타일카드 목록")
    @GetMapping("/card/hit")
    public ListResult<StyleCardCounter> styleCardsOrderByViewCount(){

        List<StyleCardCounter> countList = styleCardService.cardCountList();

        List<StyleCardCounter> topFive =  countList
                               .stream()
                               .sorted(Comparator.comparingLong(StyleCardCounter::getViewCount).reversed())
                               .limit(5)
                               .collect(Collectors.toList());

       List<StyleCardInfo> topFiveCards = styleCardService.cardInfosByIds(topFive.stream().map(StyleCardCounter::getCardId).collect(Collectors.toList()));

       topFive.forEach(styleCardCounter ->
               styleCardCounter.setCardName(topFiveCards.stream()
                       .filter(styleCardInfo ->
                               styleCardCounter.getCardId().equals(styleCardInfo.getCardId()))
                       .findAny()
                       .orElseThrow(CResourceNotExistException::new)
                       .getTitle()
               )
       );


        return responseService.getListResult(topFive);

    }








}
