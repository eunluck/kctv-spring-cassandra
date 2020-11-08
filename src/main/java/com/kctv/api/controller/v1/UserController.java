package com.kctv.api.controller.v1;


import com.kctv.api.advice.exception.*;
import com.kctv.api.config.security.JwtTokenProvider;
import com.kctv.api.entity.user.UserInfo;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.model.response.LoginResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.model.swagger.UserUpdateEx;
import com.kctv.api.service.EmailService;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;


@Api(tags = {"01 - User API"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class UserController {

    private final JwtTokenProvider jwtTokenProvider;
    private final ResponseService responseService;
    private final UserService userService;
    private final EmailService emailService;

    @ApiOperation(value = "회원가입 API", notes = "회원가입")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userInfo", value = "수정할 데이터 body", dataType = "SignUpEx", required = true),
    })
    @PostMapping("/signup")
    public SingleResult<UserInfo> signUp(@RequestBody UserInfo userInfo){


        if (!userInfo.getUserEmailType().equals("user")&&userService.userSnsLoginService(userInfo.getUserSnsKey()).isPresent()){
            throw new COverlapSnsKey();
        }

       SingleResult<UserInfo>  result = responseService.getSingleResult(userService.userSignUpService(userInfo));
        result.setMessage("이메일로 인증 링크를 보내드렸습니다. 회원가입을 완료해주세요.");

    return result;

    }



    @ApiOperation(value = "Header에 실린 TOKEN정보로 로그인한 회원 정보 출력", notes = "추가정보 수정")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @GetMapping("/user")
    public SingleResult<UserInfo> getUserByUUID(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UUID uuid = UUID.fromString(authentication.getName());
        UserInfo user = Optional.ofNullable(userService.findByUserId(uuid)).orElseThrow(CUserNotFoundException::new);



        return responseService.getSingleResult(user);
    }


    @ApiOperation(value = "회원가입 후 추가정보 입력(업데이트)API", notes = "추가정보 수정")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "userInfo", value = "수정할 데이터 body", dataType = "UserUpdateEx", required = true)
    })
    @PutMapping("/user")
    public CommonResult userUpdate(@ApiParam(hidden = true)@RequestBody UserInfo userInfo){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        userInfo.setUserId(UUID.fromString(authentication.getName()));

        userService.userUpdateService(userInfo);

        return responseService.getSuccessResult();
    }


    @ApiOperation(value = "로그인 api", notes = "정보 조회 및 JWT 토큰을 발급한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginRequest", value = "- mailType이 user일 경우: email,emailType,password입력. \n - SNS유저일 경우 : SnsKey만 입력", dataType = "LoginEx", required = true)

    })
    @PostMapping("/login")
    public LoginResult<UserInfo> userLogin(@RequestBody UserInfo loginRequest){
        
        if(loginRequest.getUserEmailType().equals("user")){
            UserInfo user = userService.checkByEmail(loginRequest.getUserEmail(),loginRequest.getUserEmailType()).orElseThrow(CNotFoundEmailException::new);
            UserInfo loginUser = userService.userLoginService(user.getUserEmail(),user.getUserEmailType(),loginRequest.getUserPassword()).orElseThrow(CIncorrectPasswordException::new);
            LoginResult<UserInfo> resultUser = responseService.getLoginResult(loginUser);
            resultUser.setToken(jwtTokenProvider.createToken(String.valueOf(loginUser.getUserId()),loginUser.getRoles()));

            return resultUser;
        }else{
            UserInfo snsLoginUser = userService.userSnsLoginService(loginRequest.getUserSnsKey()).orElseThrow(CUserNotFoundException::new);
            LoginResult<UserInfo> snsUser = responseService.getLoginResult(snsLoginUser);
            snsUser.setToken(jwtTokenProvider.createToken(String.valueOf(snsLoginUser.getUserId()),snsLoginUser.getRoles()));

            return snsUser;
        }

    }

    @ApiOperation(value = "이메일 중복 검사", notes = "가입된 이메일인지 확인한다. ")
    @GetMapping("/check/{email}/{emailType}")
    public CommonResult checkByEmail(@ApiParam(value = "이메일",required = true) @PathVariable("email") String email,
                                     @ApiParam(value = "이메일타입(ex:user,kakao,facebook)",required = true) @PathVariable("emailType") String emailType) {

        Optional<UserInfo> user = userService.checkByEmail(email,emailType);

        if (!user.isPresent()) {
             CommonResult result = responseService.getSuccessResult();
             result.setMessage("사용 가능한 이메일입니다.");
            return result;
        } else {
            SingleResult<UserInfo> result = responseService.getSingleResult(user.get());
            result.setMessage("중복된 이메일입니다.");
            return result;
        }
    }

    @ApiOperation(value = "토큰테스트용 유저 LIST", notes = "Header에 토큰을 검사하여 유저리스트를 호출한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/users")
    public ListResult<UserInfo> getAllUser(){



        return responseService.getListResult(userService.getAllUserService());
    }


    @GetMapping("/verify/{key}")
    public CommonResult getVerify(@PathVariable("key") String key){

        userService.verifyEmail(key);

        return responseService.getSuccessResult();
    }





}
