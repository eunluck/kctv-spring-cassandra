package com.kctv.api.controller.v1;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.model.place.PlaceInfoEntity;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.model.stylecard.StyleCardCounterDto;
import com.kctv.api.model.stylecard.StyleCardInfoEntity;

import com.kctv.api.model.user.UserInfoDto;
import com.kctv.api.model.user.UserInfoEntity;
import com.kctv.api.model.user.UserInterestTagEntity;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.model.response.PlaceListResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.PlaceService;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.StyleCardService;
import com.kctv.api.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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


    @ApiOperation(value = "또래들의 LifeStyle", notes = "내 나이대가 포함되는 라이프스타일 목록을 호출한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @GetMapping("/card/age/me")
    public CommonResult getStyleCardMyListByAge(@AuthenticationPrincipal UserInfoEntity me) {
        /*Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoEntity me = (UserInfoEntity)authentication.getPrincipal();*/

        UserInfoDto dto = new UserInfoDto(me);

        if (Strings.isNullOrEmpty(dto.getAges())){
            return responseService.getFailResult(-1025,"나이를 입력해주세요.");
        }

        List<StyleCardCounterDto> counterDtoList = styleCardService.cardCountListByWeek(365L);

        List<StyleCardInfoEntity> myAgesStyleCard = styleCardService.getStyleCardListAllService()
                .stream()
                .filter(styleCardInfoEntity ->
                        styleCardInfoEntity.getAges()
                                .contains(dto.getAges()))
                .sorted(Comparator.comparingLong(styleCardInfoEntity ->
                        counterDtoList
                                .stream()
                                .filter(styleCardCounterDto ->
                                        styleCardCounterDto
                                                .getCardId().equals(styleCardInfoEntity.getCardId()))
                                .findFirst()
                                .orElseThrow(CResourceNotExistException::new)
                                .getViewCount()))
                .collect(Collectors.toList());

        return responseService.getListResult(myAgesStyleCard);

    }



    @ApiOperation(value = "계정에 등록된 태그를 통해 styleCard를 조회", notes = "태그에 충족하는 스타일카드를 검색한다. 태그를 등록하지 않았을 시 랜덤으로 생성된다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @GetMapping("/card/me")
    public ListResult<StyleCardInfoEntity> getStyleCardMyList() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("getStyleCardMyList:::" + authentication.getName());
        UUID uuid = UUID.fromString(authentication.getName());

        UserInterestTagEntity connectionUser = userService.getUserInterestTag(uuid).orElseGet(() -> new UserInterestTagEntity(uuid, null, Sets.newHashSet())); // 유저의 태그들을 뽑아옴. 태그가없을시 빈 배열 생성
        UserInfoDto userInfoDto = new UserInfoDto(userService.findByUserId(uuid), new ArrayList<>(connectionUser.getTags())); // 유저객체를 위 태그와 합침
        System.out.println(userInfoDto);
        String filterAge = Optional.ofNullable(userInfoDto.getAges()).orElseGet(() -> "20대"); //유저가 가지고있는 정보로 필터를 생성
        String filterGender = Optional.ofNullable(userInfoDto.getUserGender()).orElseGet(() -> "male");

        if (!CollectionUtils.isEmpty(userInfoDto.getTags())) {
            List<StyleCardInfoEntity> filteringList = styleCardService.getCardByTagsService(new ArrayList<>(userInfoDto.getTags()))
                    .stream()
                    .filter(styleCardInfo -> styleCardInfo.getAges().contains(filterAge))
                    .filter(styleCardInfo -> styleCardInfo.getGender().contains(filterGender))
                    .limit(10L)
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(filteringList)) {
                List<StyleCardInfoEntity> randomList = styleCardService.getStyleCardListAllService().stream()
                        .limit(10L)
                        .collect(Collectors.toList());
                Collections.shuffle(randomList);

                return responseService.getListResult(randomList);
            }
            return responseService.getListResult(filteringList);
        } else {
            List<StyleCardInfoEntity> randomList = styleCardService.getStyleCardListAllService().stream()
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
    public ListResult<StyleCardInfoEntity> getStyleCardRelevant(@PathVariable("cardId") UUID cardId) {

        //TODO 정직하게 구현 필요
        List<StyleCardInfoEntity> randomList = styleCardService.getStyleCardListAllService().stream().limit(5).collect(Collectors.toList());
        Collections.shuffle(randomList);
        return responseService.getListResult(randomList);

    }

    /* tag를 통해 작성되어 있는 style Card 목록을 가져옴*/
    @ApiOperation(value = "(타인의 발견)태그를 통해 StyleCard를 검색.", notes = "태그에 충족하는 스타일카드를 검색한다.")
    @GetMapping("/card/tags/{tag}")
    public ListResult<StyleCardInfoEntity> getStyleCardList(@ApiParam(value = "검색할 태그 입력(콤마','로 구분)", defaultValue = "제주여행,따뜻한")
                                                            @PathVariable("tag") String tags,
                                                            @RequestParam(required = false,name = "gender")String gender,
                                                            @RequestParam(required = false,name = "age")String age) {

        List<String> tagArr = Arrays.asList(tags.split(","));

        if (tagArr.size() > 0) {
            if (!Strings.isNullOrEmpty(gender) && !Strings.isNullOrEmpty(age)){
                return responseService.getListResult(styleCardService.getCardByTagsService(tagArr)
                        .stream()
                        .filter(styleCardInfoEntity -> styleCardInfoEntity.getGender().contains(gender) && styleCardInfoEntity.getAges().contains(age))
                        .collect(Collectors.toList()));
            }else {
            return responseService.getListResult(styleCardService.getCardByTagsService(tagArr));

            }

        } else {
            return responseService.getListResult(Lists.newArrayList());
        }

    }

    @ApiOperation(value = "card id로 특정 카드 상세보기", notes = "uuid를 통해 특정 카드를 검색한다.")
    @GetMapping("/card/{cardId}")
    public SingleResult<?> getCardById(@ApiParam(value = "검색할 Card UUID 입력", defaultValue = "fc35a91b-3bb2-4a55-8a45-3b03df9e797d")
                                       @PathVariable("cardId") UUID cardId) {

        return responseService.getSingleResult(styleCardService.getCardById(cardId));

    }


    @ApiOperation(value = "LifeStyleCardList", notes = "등록된 모든 카드를 조회한다.")
    @GetMapping("/card")
    public ListResult<StyleCardInfoEntity> getStyleCardAll() {

        return responseService.getListResult(styleCardService.getStyleCardListAllService());

    }


    @ApiOperation(value = "기준에 충족되는 가게리스트 검색", notes = "스타일카드에 해당되는 가게 리스트를 조회한다.<br/>" +
            "card에 직접 입력된 가게 리스트 조회: \"/card/{cardId}/place?type=id\") <br/>" +
            "card의 태그를 기반으로 가게 리스트 조회: \"/card/{cardId}/place?type=tag\"    ")
    @GetMapping("/card/{cardId}/place")
    public PlaceListResult getPlaceByTags(@ApiParam(value = "검색할 Card UUID 입력", defaultValue = "fc35a91b-3bb2-4a55-8a45-3b03df9e797d")
                                          @PathVariable("cardId") UUID cardId,
                                          @RequestParam(required = false, value = "type", defaultValue = "id") String type) {

        //타입을 입력받게되면 tag기반으로 검색된 플레이스 리스트가 나타난다.


        StyleCardInfoEntity cardInfo = styleCardService.getCardById(cardId);
        if (CollectionUtils.isEmpty(cardInfo.getPlaceId())) {
            type = "tag";
        }

        List<PlaceInfoEntity> placeInfoEntityList = Lists.newArrayList();


        if ("tag".equals(type)) {
            placeInfoEntityList.addAll(placeService.getPartnerInfoListByTagsService(Lists.newArrayList(cardInfo.getTags())));
        } else if ("id".equals(type)) {
            placeInfoEntityList.addAll(placeService.getPlaceListByIdIn(Lists.newArrayList(cardInfo.getPlaceId())));
        }


        return responseService.getPlaceListResult(cardInfo, placeInfoEntityList);

    }


    private List<StyleCardCounterDto> styleCardCounterDtoCache = null;
    private Long styleCardCounterCacheUpdateTs = 0L;

    @ApiOperation(value = "최근 일주일간 스타일카드 조회수 랭킹 TOP5", notes = "가장 많이 조회된 스타일카드 목록(10분마다 갱신)")
    @GetMapping("/card/hit")
    public ListResult<StyleCardCounterDto> styleCardsOrderByViewCountDay(@RequestParam(value = "minusDays",defaultValue = "7")long day) {

        if (styleCardCounterCacheUpdateTs < (System.currentTimeMillis() - 10000)) {


            List<StyleCardCounterDto> countList = styleCardService.cardCountListByWeek(day);

            if (countList.size() == 0){
                return responseService.getListResult(Lists.newArrayList());
            }

            List<StyleCardCounterDto> topFive = countList
                    .stream()
                    .sorted(Comparator.comparingLong(StyleCardCounterDto::getViewCount).reversed())
                    .limit(5)
                    .collect(Collectors.toList());

            List<StyleCardInfoEntity> topFiveCards = styleCardService.cardInfosByIds(countList.stream().map(StyleCardCounterDto::getCardId).collect(Collectors.toList()));

            for (StyleCardCounterDto styleCardCounterDto : topFive){
                styleCardCounterDto.setCardName(topFiveCards.stream().filter(styleCardInfo -> styleCardInfo.getCardId().equals(styleCardCounterDto.getCardId())).findFirst().get().getTitle());
                styleCardCounterDto.setCoverImage(topFiveCards.stream().filter(styleCardInfo -> styleCardInfo.getCardId().equals(styleCardCounterDto.getCardId())).findFirst().get().getCoverImage());
            }

            styleCardCounterDtoCache = topFive;

        }
        return responseService.getListResult(styleCardCounterDtoCache);
    }


}
