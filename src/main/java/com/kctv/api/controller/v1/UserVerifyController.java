package com.kctv.api.controller.v1;


import com.kctv.api.model.user.UserInfoEntity;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@Api(tags = {"02. UserEmailVerify API"})
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/v1")
public class UserVerifyController {

    private final ResponseService responseService;
    private final UserService userService;


    @ApiOperation(value = "이메일인증 API", notes = "메일로 보낸 링크를 통해 이메일을 인증한다.")
    @GetMapping("/verify/{key}")
    public String getVerify(@PathVariable("key") String key){

        try {
            userService.newVerifyEmail(key);
            return "successverify";
        }catch (Exception e){
            e.printStackTrace();

        }
        return "successverify";
    }

    @ApiOperation(value = "이메일 인증 메일 재전송", notes = "등록했던 이메일로 인증 메일을 재전송한다. (인증링크유효기간3분)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @GetMapping("/verify/resend")
    @ResponseBody
    public CommonResult reSendMail() throws UnsupportedEncodingException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserInfoEntity user = (UserInfoEntity) authentication.getPrincipal();


        userService.userVerifyEmailResend(user);
        //userService.sendVerificationMail(user);

        return responseService.getSuccessResult();
    }


    //TODO // 이메일을 입력한다 > 임시비밀번호가 이메일로 발급된다 (기존 비번 그대로임) > 임시비밀번호로 로그인하면 비밀번호 변경을 한다


    @ApiOperation(value = "비밀번호찾기", notes = "입력한 이메일로 임시비밀번호를 발급한다.")
    @ResponseBody
    @PutMapping("/find/password/{email}/{emailType}")
    public CommonResult sendTempPassword(@ApiParam(value = "email",example = "test@gmail.com")@PathVariable("email") String email,
                                 @ApiParam(value = "emailType",example = "user")@PathVariable("emailType")String emailType) throws MessagingException {

        userService.sendTempPassword(email,emailType);
        CommonResult result = responseService.getSuccessResult();
        result.setMessage("이메일로 임시 비밀번호를 발급했습니다. 로그인 후 비밀번호를 변경해주세요.");

        return result;
    }


}
