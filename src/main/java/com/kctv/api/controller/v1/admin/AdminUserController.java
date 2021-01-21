package com.kctv.api.controller.v1.admin;

import com.google.common.collect.Sets;
import com.kctv.api.entity.user.UserInfoDto;
import com.kctv.api.entity.user.UserInterestTag;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Api(tags = {"12. Admin User API"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value =  "/v1/admin/user")
public class AdminUserController {


    private final ResponseService responseService;
    private final UserService userService;

    @ApiOperation(value = "관리자용 유저목록 조회", notes = "관리자용 유저리스트를 호출한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/list")
    public ListResult<UserInfoDto> getAllUser() {


        return responseService.getListResult(userService.getAllUserService().stream().map(userInfo ->
                new UserInfoDto(userInfo, userService.getUserInterestTag(userInfo.getUserId())
                        .orElseGet(() -> new UserInterestTag(userInfo.getUserId(), null, Sets.newHashSet()))
                        .getTags().stream().collect(toList())))
                .collect(toList()));
    }


    @ApiOperation(value = "관리자용 유저 상세 조회", notes = "유저의 ID를 통해 상세 조회한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/{userId}")
    public SingleResult<UserInfoDto> getUserById(@PathVariable UUID userId) {


        return responseService.getSingleResult(new UserInfoDto(userService.findByUserId(userId)));


    }
}