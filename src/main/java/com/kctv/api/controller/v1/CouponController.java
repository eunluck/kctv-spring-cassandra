package com.kctv.api.controller.v1;

import com.kctv.api.model.ap.FindApRequest;
import com.kctv.api.model.coupon.CouponEntity;
import com.kctv.api.model.coupon.UserByCouponDto;
import com.kctv.api.model.coupon.UserByCouponEntity;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.model.user.UserInfoEntity;
import com.kctv.api.service.CouponService;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.WakeupPermissionService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/coupon")
public class CouponController {


    private final ResponseService responseService;
    private final WakeupPermissionService wakeupPermissionService;
    private final CouponService couponService;


    @ApiOperation(value = "내 쿠폰 리스트", notes = "내 쿠폰리스트다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @GetMapping("/me")
    public CommonResult myCouponList(@AuthenticationPrincipal UserInfoEntity userInfoEntity){


        return responseService.getListResult(couponService.getCouponByUser(userInfoEntity.getUserId()));

    }
}
