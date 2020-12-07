package com.kctv.api.controller.v1;


import com.kctv.api.advice.exception.CPartnerNotFoundException;
import com.kctv.api.entity.ap.PartnerInfo;
import com.kctv.api.entity.ap.WifiInfo;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.PlaceService;
import com.kctv.api.service.ResponseService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Api(tags = {"07. Places API"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class PlaceController {

    private final PlaceService placeService;
    private final ResponseService responseService;

    @ApiOperation(value = "현재 위치와 가까운 가게 검색", notes = "현재 접속중인 ap의 가게 ID를 통해 주변의 가까운 가게를 검색한다.")
    @GetMapping("/place/{partnerId}/wifi/{distance}")
    public ListResult<WifiInfo> getGeo(@ApiParam(value = "현재 접속중인 AP의 가게ID",defaultValue = "0c888459-3a05-4396-afc6-83ba60b4908c") @PathVariable("partnerId") UUID partnerUuid,
                                       @ApiParam(value = "검색 할 거리(km)",defaultValue = "0.3")@PathVariable("distance")Double distance){

        return responseService.getListResult(placeService.getPartnerWifiService(partnerUuid,distance));

    }

    @ApiOperation(value = "전체 Partner목록 출력", notes = "테스트용.")
    @GetMapping("/places")
    public ListResult<PartnerInfo> getPlaceAll(){

    return responseService.getListResult(placeService.getPartnerInfoListService());

    }

    @ApiOperation(value = "가게 ID를 통해 상세조회", notes = "특정 가게를 상세조회한다.")
    @GetMapping("/place/{id}")
    public SingleResult<PartnerInfo> getPlaceById(@ApiParam(value = "검색할 가게 ID",defaultValue = "b3dc4146-2827-47c0-bbc8-9fe350d4134e")@PathVariable("id")UUID uuid){

    return responseService.getSingleResult(placeService.getPartnerByIdService(uuid).orElseThrow(CPartnerNotFoundException::new));

    }

    @ApiOperation(value = "태그에 충족되는 가게 검색", notes = "태그에 충족되는 가게를 조회한다.(태그가 많이 충족되는 순서로 내림차순)")
    @GetMapping("/place/tags/{tags}")
    public ListResult<PartnerInfo> getPlaceByTags(@ApiParam(value = "검색할 태그(','로 구분.))",defaultValue = "건강,따뜻한,제주생활")@PathVariable("tags")String tags){


        List<String> tagArr = Arrays.asList(tags.split(","));


        return responseService.getListResult(placeService.getPartnerInfoListByTagsService(tagArr));

    }





}
