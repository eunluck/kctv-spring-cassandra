package com.kctv.api.controller.v1;


import com.kctv.api.entity.user.UserInfo;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
            userService.verifyEmail(key);
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
    public CommonResult reSendMail(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserInfo user = (UserInfo) authentication.getPrincipal();

        System.out.println(user.toString());

        userService.sendVerificationMail(user);

        return responseService.getSuccessResult();
    }


}
