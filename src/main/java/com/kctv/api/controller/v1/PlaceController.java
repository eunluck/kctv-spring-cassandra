package com.kctv.api.controller.v1;


import com.google.common.collect.Lists;
import com.kctv.api.advice.exception.CPartnerNotFoundException;
import com.kctv.api.model.coupon.CouponByPlaceDto;
import com.kctv.api.model.coupon.CouponEntity;
import com.kctv.api.model.coupon.UserByCouponDto;
import com.kctv.api.model.interview.OwnerInterviewEntity;
import com.kctv.api.model.place.PlaceCounterDto;
import com.kctv.api.model.place.PlaceInfoEntity;
import com.kctv.api.model.place.WifiInfoEntity;
import com.kctv.api.model.place.PlaceInfoDto;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.model.stylecard.StyleCardCounterDto;
import com.kctv.api.model.user.UserInfoEntity;
import com.kctv.api.service.CouponService;
import com.kctv.api.service.InterviewService;
import com.kctv.api.service.PlaceService;
import com.kctv.api.service.ResponseService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.*;
import java.util.stream.Collectors;

@Api(tags = {"07. Places API"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class PlaceController {

    private final PlaceService placeService;
    private final InterviewService interviewService;
    private final ResponseService responseService;
    private final CouponService couponService;





    @ApiOperation(value = "현재 위치와 가까운 가게 검색", notes = "현재 접속중인 ap의 가게 ID를 통해 주변의 가까운 가게를 검색한다.")
    @GetMapping("/place/{partnerId}/wifi/{distance}")
    public ListResult<WifiInfoEntity> getGeo(@ApiParam(value = "현재 접속중인 AP의 가게ID",defaultValue = "0c888459-3a05-4396-afc6-83ba60b4908c") @PathVariable("partnerId") UUID partnerUuid,
                                             @ApiParam(value = "검색 할 거리(km)",defaultValue = "0.3")@PathVariable("distance")Double distance){

        return responseService.getListResult(placeService.getPartnerWifiService(partnerUuid,distance));

    }

    @ApiOperation(value = "전체 Partner목록 출력", notes = "테스트용.")
    @GetMapping("/places")
    public ListResult<PlaceInfoEntity> getPlaceAll(){

    return responseService.getListResult(placeService.getPartnerInfoListService());

    }

    @ApiOperation(value = "가게 ID를 통해 상세조회 (메뉴 포함)", notes = "특정 가게를 상세조회한다.")
    @GetMapping("/place/{id}")
    public SingleResult<PlaceInfoDto> getPlaceById(@ApiParam(value = "검색할 가게 ID",defaultValue = "ebe58bff-dd68-434c-9687-cfacda45aefb")@PathVariable("id")UUID uuid){

        PlaceInfoEntity placeInfoEntity = placeService.getPartnerByIdService(uuid).orElseThrow(CPartnerNotFoundException::new);
        Optional<OwnerInterviewEntity> ownerInterviewEntity = interviewService.findByOwnerInterviewEntityByPlaceId(placeInfoEntity.getPartnerId());
        PlaceInfoDto dto = new PlaceInfoDto(placeInfoEntity, placeService.getMenuByPartnerId(uuid));

        dto.setOwnerInterview(ownerInterviewEntity.orElseGet(() -> null));

    return responseService.getSingleResult(dto);

    }

    @ApiOperation(value = "태그에 충족되는 가게 검색", notes = "태그에 충족되는 가게를 조회한다.(태그가 많이 충족되는 순서로 내림차순)")
    @GetMapping("/place/tags/{tags}")
    public ListResult<PlaceInfoEntity> getPlaceByTags(@ApiParam(value = "검색할 태그(','로 구분.))",defaultValue = "건강,따뜻한,제주생활")@PathVariable("tags")String tags){


        List<String> tagArr = Arrays.asList(tags.split(","));

        if(CollectionUtils.isNotEmpty(tagArr)){

        return responseService.getListResult(placeService.getPartnerInfoListByTagsService(tagArr));
        }else{
            return responseService.getListResult(placeService.getPartnerInfoListService());
        }

    }

    @ApiOperation(value = "태그에 충족되는 가게 검색", notes = "태그에 충족되는 가게를 조회한다.(태그가 많이 충족되는 순서로 내림차순)")
    @GetMapping("/place/search/tag/{tags}")
    public ListResult<PlaceInfoEntity> newGetPlaceByTags(@ApiParam(value = "검색할 태그(','로 구분.))",defaultValue = "건강,따뜻한,제주생활")@PathVariable("tags")String tags){


        List<String> tagArr = Arrays.asList(tags.split(","));

        if(CollectionUtils.isNotEmpty(tagArr)){

            return responseService.getListResult(placeService.getPartnerInfoListByTagsService(tagArr));
        }else{
            return responseService.getListResult(placeService.getPartnerInfoListService());
        }

    }


    private List<PlaceCounterDto> placeCounterDtoCache = null;
    private Long placeCounterCacheUpdateTs = 0L;

    @ApiOperation(value = "최근 일주일간 장소 조회수 랭킹 TOP5", notes = "가장 많이 조회된 장소 목록(10분마다 갱신)")
    @GetMapping("/place/hit")
    public ListResult<PlaceCounterDto> placeOrderByViewCountDay(@RequestParam(value = "minusDays",defaultValue = "7")long day) {
        //if (placeCounterCacheUpdateTs < (System.currentTimeMillis() - 10000)) {

            List<PlaceCounterDto> counterDtoList = placeService.placeCountListByWeek(day);


            if (counterDtoList.size() == 0){
                return responseService.getListResult(Lists.newArrayList());
            }

            List<PlaceCounterDto> topFive = counterDtoList
                    .stream()
                    .sorted(Comparator.comparingLong(PlaceCounterDto::getViewCount).reversed())
                    .limit(5)
                    .collect(Collectors.toList());

            List<PlaceInfoEntity> placeInfoEntities = placeService.getPlaceListByIdIn(counterDtoList.stream().map(PlaceCounterDto::getPlaceId).collect(Collectors.toList()));

            for (PlaceCounterDto placeCounterDto : topFive) {
                placeCounterDto.setPlaceName(placeInfoEntities.stream().filter(placeInfo -> placeInfo.getPartnerId().equals(placeCounterDto.getPlaceId())).findFirst().get().getBusinessName());
                placeCounterDto.setPlaceType(placeInfoEntities.stream().filter(placeInfo -> placeInfo.getPartnerId().equals(placeCounterDto.getPlaceId())).findFirst().get().getStoreType());
                placeCounterDto.setCoverImage(placeInfoEntities.stream().filter(placeInfo -> placeInfo.getPartnerId().equals(placeCounterDto.getPlaceId())).findFirst().get().getCoverImage());

            }
           // placeCounterDtoCache = topFive;
       // }

        //return responseService.getListResult(placeCounterDtoCache);
        return responseService.getListResult(topFive);
    }


    @ApiOperation(value = "가게의 쿠폰 검색", notes = "가게에서 받을 수 있는 쿠폰 목록")
    @GetMapping("/place/{placeId}/coupon")
    public ListResult<CouponEntity> couponByPlace(@AuthenticationPrincipal UserInfoEntity loginUser, @PathVariable("placeId")UUID placeId){



        List<UserByCouponDto> myCouponList = couponService.getCouponByUser(loginUser.getUserId());

        placeService.getCouponByPlace(placeId).stream().filter(CouponEntity::isState).map(CouponByPlaceDto::new).forEach(couponByPlaceDto ->
                couponByPlaceDto.setCanSaved(!myCouponList.contains(couponByPlaceDto.getCouponId()))
                );


        return responseService.getListResult(placeService.getCouponByPlace(placeId));

        // 사용자가 이미 다운로드 한 쿠폰 false
        // 쿠폰의 사용여부에따라 false

    }




}
