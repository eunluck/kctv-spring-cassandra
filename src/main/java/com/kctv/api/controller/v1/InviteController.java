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
            return responseService.getFailResult(-11,"유효하지 않는 코드입니다.");
        }else if(inviteService.inviteDoubleCheck(uuid,user.get().getUserId())) {
            return responseService.getFailResult(-12,"이미 추천했던 사용자입니다.");
        }else {
            if (inviteService.saveInviteCode(new InviteFriends(uuid,user.get().getUserId(),new Date()))) {
                //추천인 코드는 여러명을 입력할 수 있다.(완)
                //한번 추천한 사람에게 중복 추천할 수 없다. (완)
                //추천인과 추천등록한사람은 100MB씩 제공받는다.(완)
                //TODO 이곳에  100메가 추가 로직만 추가하면된다.
                return responseService.getSuccessResult();
            }else{
                return responseService.getFailResult(-19,"친구 추천이 실패했습니다. 관리자에게 문의바랍니다.");
            }
        }
    }




}
