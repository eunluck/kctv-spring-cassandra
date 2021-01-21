package com.kctv.api.controller.v1;

import com.kctv.api.entity.user.InviteFriends;
import com.kctv.api.entity.user.UserInfo;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.service.InviteService;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;



@Api(tags = {"15. invite API"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/invite")
public class InviteController {

    private final ResponseService responseService;
    private final InviteService inviteService;


    @ApiOperation(value = "추천인 코드 입력", notes = "추천인 코드를 입력하여 데이터를 보너스 데이터를 제공받는다. ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @PutMapping("/{friendCode}")
    public CommonResult acceptInvitation(@ApiParam(value = "추천인 코드",required = true, defaultValue = "8d0a10") @PathVariable("friendCode")String code){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID uuid = UUID.fromString(authentication.getName());


        Optional<UserInfo> user = inviteService.findUserByCode(code);

        if (!user.isPresent()){
            return responseService.getFailResult(-1,"유효하지 않는 코드입니다.");
        }else {
            if (inviteService.saveInviteCode(new InviteFriends(uuid,user.get().getUserId(),new Date()))) {
                return responseService.getSuccessResult();
            }else
                return responseService.getFailResult(-2,"친구 추천이 실패했습니다. 관리자에게 문의바랍니다.");
        }
    }




}
