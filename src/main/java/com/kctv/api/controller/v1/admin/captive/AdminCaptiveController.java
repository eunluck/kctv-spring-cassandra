package com.kctv.api.controller.v1.admin.captive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.entity.admin.FaqRequest;
import com.kctv.api.entity.admin.FaqTable;
import com.kctv.api.entity.admin.QnaAnswer;
import com.kctv.api.entity.admin.ad.CaptivePortalAdEntity;
import com.kctv.api.entity.place.PlaceInfo;
import com.kctv.api.entity.place.PlaceInfoDto;
import com.kctv.api.entity.place.PlaceInfoVo;
import com.kctv.api.entity.qna.QnaByUserEntity;
import com.kctv.api.entity.stylecard.StyleCardInfo;
import com.kctv.api.entity.stylecard.admin.StyleCardVo;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Api(tags = {"11. Admin CaptivePortal API"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/admin/captive")
public class AdminCaptiveController {


    private final ResponseService responseService;
    private final StorageService storageService;
    private final CaptivePortalAdService captivePortalAdService;



    @ApiOperation(value = "캡티브 포탈 광고 이미지 등록", notes = "캡티브포탈이미지를 등록한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "link", value = "이미지 클릭 시 이동할 링크 주소", dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "imgFile", value = "광고이미지", dataType = "file", paramType = "form"),
            @ApiImplicitParam(name = "startDate", value = "광고시작일", dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "endDate", value = "광고마감일", dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "status", value = "광고상태", dataType = "String", paramType = "form", defaultValue = "inactive")
    })
    @PostMapping(value = "/ad", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SingleResult<CaptivePortalAdEntity> captiveImgPost(@ModelAttribute CaptiveRequest request) throws IOException {


        return responseService.getSingleResult(storageService.saveAdImg(request));

    }


    @ApiOperation(value = "캡티브 포탈 목록 조회", notes = "캡티브 포탈 목록을 조회한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @GetMapping(value = "/ad")
    public ListResult<CaptivePortalAdEntity> captiveAdList() {


        return responseService.getListResult(captivePortalAdService.findAllAd());

    }



    @ApiOperation(value = "캡티브 포탈 이미지 삭제", notes = "캡티브 포탈 이미지를 삭제한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @DeleteMapping(value = "/ad/{adId}")
    public SingleResult<CaptivePortalAdEntity> captiveAdDelete(@PathVariable("adId")UUID adId) throws IOException {

        CaptivePortalAdEntity result = captivePortalAdService.deleteAd(adId);
        storageService.deleteAdFile(result);

        return responseService.getSingleResult(result);
    }

    @ApiOperation(value = "캡티브 포탈 상태를 Active로 변경", notes = "캡티브 포탈 이미지 상태를 변경한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @PutMapping(value = "/ad/{adId}/active")
    public SingleResult<CaptivePortalAdEntity> captiveAdActive(@PathVariable("adId")UUID adId) {

        CaptivePortalAdEntity result = captivePortalAdService.activeAd(adId);

        return responseService.getSingleResult(result);
    }



    @ApiOperation(value = "캡티브 포탈 상태 Inactive로 변경", notes = "캡티브 포탈 이미지 상태를 변경한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @PutMapping(value = "/ad/{adId}/inactive")
    public SingleResult<CaptivePortalAdEntity> captiveAdInactive(@PathVariable("adId")UUID adId) {

        CaptivePortalAdEntity result = captivePortalAdService.inactiveAd(adId);

        return responseService.getSingleResult(result);
    }



    @ApiOperation(value = "캡티브 포탈 상태 변경", notes = "캡티브 포탈 이미지 상태를 변경한다.(response: Active or inactive)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @PutMapping(value = "/ad/{adId}/status")
    public SingleResult<CaptivePortalAdEntity> captiveUpdateStatus(@PathVariable("adId")UUID adId) {

        CaptivePortalAdEntity result = captivePortalAdService.updateStatus(adId);

        return responseService.getSingleResult(result);
    }



}
