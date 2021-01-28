package com.kctv.api.controller.v1;

import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.entity.place.PlaceInfo;
import com.kctv.api.entity.visit.UserVisitHistoryEntity;
import com.kctv.api.entity.visit.VisitCount;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.model.visit.UserVisitHistoryDto;
import com.kctv.api.model.visit.VisitCountDto;
import com.kctv.api.service.PlaceService;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.UserVisitHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Api(tags = {"16. UserVisitHistory API"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/user/visit")
public class UserVisitController {

    private final UserVisitHistoryService userVisitHistoryService;
    private final ResponseService responseService;
    private final PlaceService placeService;

    @ApiOperation(value = "방문한 가게 리스트", notes = "내가 방문했던 가게 리스트 20개를 조회한다")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping(value = "/")
    public ListResult<UserVisitHistoryDto> visitHistory(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UUID uuid = UUID.fromString(authentication.getName());

        List<UserVisitHistoryEntity> visitHistoryEntityList = userVisitHistoryService.findHistoryByUserId(uuid);
       List<PlaceInfo> list = placeService.getPlaceListByIdIn(visitHistoryEntityList.stream().map(UserVisitHistoryEntity::getPlaceId).collect(Collectors.toList()));

        return responseService.getListResult(visitHistoryEntityList
                .stream()
                .map(userVisitHistoryEntity ->
                        new UserVisitHistoryDto(userVisitHistoryEntity,userVisitHistoryEntity.getTimestamp(),
                                list.stream()
                                        .filter(placeInfo ->
                                                placeInfo.getPartnerId().equals(userVisitHistoryEntity.getPlaceId()))
                                        .findFirst()
                                        .orElseThrow(CResourceNotExistException::new)))
                .sorted(Comparator.comparingLong(UserVisitHistoryDto::getVisitDate))
                .collect(Collectors.toList()));
    }


    @ApiOperation(value = "자주 방문한 가게 리스트", notes = "자주 방문했던 장소 5곳을 조회한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/user/visit/count")
    public ListResult<?> visitCountByPlace(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UUID uuid = UUID.fromString(authentication.getName());

        List<VisitCount> visitCountList = userVisitHistoryService.visitCounts(uuid);
        List<PlaceInfo> visitPlaceList = placeService.getPlaceListByIdIn(visitCountList.stream().map(VisitCount::getPlaceId).collect(Collectors.toList()));


        return responseService.getListResult(
                visitCountList
                        .stream()
                        .map(visitCount ->
                                new VisitCountDto(uuid,visitPlaceList
                                        .stream()
                                        .filter(placeInfo -> placeInfo.getPartnerId().equals(visitCount.getPlaceId()))
                                        .findFirst()
                                        .orElseThrow(CResourceNotExistException::new),visitCount.getVisitCount()))
                        .sorted(Comparator.comparingLong(VisitCountDto::getVisitCount))
                        .collect(Collectors.toList()));
    }












}