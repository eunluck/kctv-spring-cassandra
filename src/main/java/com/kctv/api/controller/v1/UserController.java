package com.kctv.api.controller.v1;


import com.google.common.collect.Sets;
import com.kctv.api.advice.exception.*;
import com.kctv.api.config.security.JwtTokenProvider;


import com.kctv.api.model.admin.ManagerDto;
import com.kctv.api.model.user.PasswordUpdateRequest;
import com.kctv.api.model.user.UserInfoEntity;
import com.kctv.api.model.user.UserInfoDto;
import com.kctv.api.model.user.UserInterestTagEntity;

import com.kctv.api.model.ap.WakeupPermissionEntity;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.model.response.LoginResult;
import com.kctv.api.model.response.SingleResult;


import com.kctv.api.model.tag.Role;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.UserService;
import com.kctv.api.service.WakeupPermissionService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.*;


@Api(tags = {"01. User API"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class UserController {

    private final JwtTokenProvider jwtTokenProvider;
    private final ResponseService responseService;
    private final UserService userService;
    private final WakeupPermissionService wakeupPermissionService;




    @ApiOperation(value = "회원가입 API", notes = "회원가입 후 인증메일을 발송한다. (인증링크유효기간3분)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userInfo", value = "수정할 데이터 body", dataType = "SignUpEx", required = true)
    })
    @PostMapping(value = "/signup", consumes = "application/json;charset=utf-8", produces = "application/json;charset=utf-8")
    public SingleResult<UserInfoEntity> signUp(@ApiIgnore @RequestBody UserInfoEntity userInfoEntity) throws GeneralSecurityException, UnsupportedEncodingException {

        Optional<UserInfoEntity> requestUser = userService.checkByEmail(userInfoEntity.getUserEmail(), userInfoEntity.getUserEmailType());

        if (requestUser.isPresent()) {
            throw new CUserExistException();
        }
        if (!"user".equals(userInfoEntity.getUserEmailType()) && userService.userSnsLoginService(userInfoEntity.getUserSnsKey()).isPresent()) {
            throw new COverlapSnsKey();
        }

        UserInfoEntity signUpUser = userService.userSignUpService(userInfoEntity);
        SingleResult<UserInfoEntity> result = responseService.getSingleResult(userService.findByUserId(signUpUser.getUserId()));
        if ("user".equals(result.getData().getUserEmailType()))
            result.setMessage("이메일로 인증 링크를 보내드렸습니다. 회원가입을 완료해주세요.");

        return result;

    }



    @ApiOperation(value = "비밀번호 변경 API", notes = "비밀번호를 변경한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @PutMapping("/user/password")
    public SingleResult<UserInfoEntity> changePassword(@RequestBody PasswordUpdateRequest request){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserInfoEntity user = (UserInfoEntity) authentication.getPrincipal();

        if (!"user".equals(user.getUserEmailType())){
            throw new CIncorrectPasswordException("sns 회원은 비밀번호 변경이 불가합니다.");
        }
        if (!userService.userPasswordMatches(user,request.getCurrentPassword())){
            throw new CIncorrectPasswordException();
        }else {
            user.updatePassword(request.getNewPassword());
            userService.userUpdatePassword(user);
        return responseService.getSingleResult(user);
        }
    }



    @ApiOperation(value = "회원탈퇴 API", notes = "사용자를 삭제한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @DeleteMapping("/user")
    public SingleResult<UserInfoEntity> removeUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserInfoEntity deleteRequest = (UserInfoEntity) authentication.getPrincipal();

        return responseService.getSingleResult(userService.deleteUserInfo(deleteRequest));

    }



    @ApiOperation(value = "내 정보 API(태그,permission 정보 포함)", notes = "Header에 실린 TOKEN정보로 로그인한 회원 정보 출력")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/user")
    public SingleResult<UserInfoDto> getUserByUUID() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserInfoEntity user = (UserInfoEntity) authentication.getPrincipal();
        UserInterestTagEntity userInterestTagEntity = userService.getUserInterestTag(user.getUserId()).orElse(new UserInterestTagEntity(user.getUserId(), null, Sets.newHashSet()));
        List<String> userTags = new ArrayList<>(userInterestTagEntity.getTags());

        WakeupPermissionEntity wakeupPermissionEntity = wakeupPermissionService.findPermissionByUserId(user.getUserId());

        return responseService.getSingleResult(new UserInfoDto(user, userTags, wakeupPermissionEntity));
    }


    @ApiOperation(value = "회원가입 후 추가정보 입력(업데이트)API", notes = "추가정보 수정")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "userInfo", value = "수정할 데이터 body", dataType = "UserUpdateEx", required = true)
    })
    @PutMapping("/user")
    public CommonResult userUpdate(@ApiIgnore @RequestBody UserInfoEntity userInfoEntity) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userInfoEntity.setUserId(UUID.fromString(authentication.getName()));

        UserInfoEntity afterUser = userService.userUpdateService(userInfoEntity);
        UserInterestTagEntity userInterestTagEntity = userService.getUserInterestTag(afterUser.getUserId()).orElseGet(() -> new UserInterestTagEntity(afterUser.getUserId(), null, Sets.newHashSet()));
        List<String> userTags = new ArrayList<>(userInterestTagEntity.getTags());
        WakeupPermissionEntity permission = wakeupPermissionService.findPermissionByUserId(afterUser.getUserId());

        return responseService.getSingleResult(new UserInfoDto(userService.findByUserId(afterUser.getUserId()), userTags, permission));
    }

    @ApiOperation(value = "로그인 api", notes = "정보 조회 및 JWT 토큰을 발급한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginRequest", value = "- mailType이 user일 경우: email,emailType,password입력. \n - SNS유저일 경우 : SnsKey만 입력", dataType = "LoginEx", required = true)

    })
    @PostMapping("/login")
    public LoginResult<?> userLogin(@RequestBody UserInfoEntity loginRequest) {

        if (loginRequest.getUserEmailType().equals("user")) {
            UserInfoEntity user = userService.checkByEmail(loginRequest.getUserEmail(), loginRequest.getUserEmailType()).orElseThrow(CNotFoundEmailException::new);
            UserInfoEntity loginUser = userService.userLoginService(user.getUserEmail(), user.getUserEmailType(), loginRequest.getUserPassword()).orElseThrow(CIncorrectPasswordException::new);
            if (Role.adminIsTrue(loginUser.getRoles())) {
                LoginResult<ManagerDto> resultManager = responseService.getLoginResult(new ManagerDto(loginUser));
                resultManager.setToken(jwtTokenProvider.createToken(String.valueOf(loginUser.getUserId()), loginUser.getRoles()));

                return resultManager;

            } else {
                LoginResult<UserInfoEntity> resultUser = responseService.getLoginResult(loginUser);

                resultUser.setEmailVerify(loginUser.getRoles().stream().noneMatch(s -> s.contains("NOT_VERIFY_EMAIL")));

                resultUser.setToken(jwtTokenProvider.createToken(String.valueOf(loginUser.getUserId()), loginUser.getRoles()));

                return resultUser;
            }
        } else {
            UserInfoEntity snsLoginUser = userService.userSnsLoginService(loginRequest.getUserSnsKey()).orElseThrow(CUserNotFoundException::new);
            LoginResult<UserInfoEntity> snsUser = responseService.getLoginResult(snsLoginUser);
            snsUser.setToken(jwtTokenProvider.createToken(String.valueOf(snsLoginUser.getUserId()), snsLoginUser.getRoles()));

            return snsUser;
        }
    }

    @ApiOperation(value = "이메일 중복 검사", notes = "가입된 이메일인지 확인한다. ")
    @GetMapping("/check/{email}/{emailType}")
    public CommonResult checkByEmail(@ApiParam(value = "이메일", required = true) @PathVariable("email") String email,
                                     @ApiParam(value = "이메일타입(ex:user,kakao,facebook)", required = true) @PathVariable("emailType") String emailType) {

        Optional<UserInfoEntity> user = userService.checkByEmail(email, emailType);

        if (!user.isPresent()) {
            CommonResult result = responseService.getSuccessResult();
            result.setMessage("사용 가능한 이메일입니다.");
            return result;
        } else {
            SingleResult<UserInfoEntity> result = responseService.getSingleResult(user.get());
            result.setMessage("중복된 이메일입니다.");
            return result;
        }
    }


    @ApiOperation(value = "계정에 태그 등록", notes = "token을 통해 계정에 관심사(태그)들을 추가(수정)한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @PostMapping("/user/tag")
    public CommonResult userInterestCreateTags(@RequestBody List<String> tags) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UUID userId = UUID.fromString(authentication.getName());


        UserInterestTagEntity saveUser = UserInterestTagEntity.builder().userId(userId).tags(Sets.newHashSet(tags)).build();
        userService.userInterestTagService(saveUser);

        return responseService.getSuccessResult();
    }


    @ApiOperation(value = "계정에 등록 된 태그 조회", notes = "token을 통해 계정에 관심사(태그)들을 조회한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @GetMapping("/user/tag/me")
    public SingleResult<UserInterestTagEntity> getUserInterestTags() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        System.out.println("디버깅getUserInterestTags::" + authentication.getName());
        UUID userId = UUID.fromString(authentication.getName());

        UserInterestTagEntity interestTag = userService.getUserInterestTag(userId).orElseGet(() -> new UserInterestTagEntity(userId, null, Sets.newHashSet()));

        return responseService.getSingleResult(interestTag);
    }



    @ApiOperation(value = "계정에 등록 된 태그 조회", notes = "token을 통해 계정에 관심사(태그)들을 조회한다.")
    @GetMapping("/user/login/{uuid}/admin")
    public LoginResult<UserInfoEntity> loginAdmin(@PathVariable("uuid") UUID uuid) {


        UserInfoEntity user = userService.findByUserId(uuid);

        LoginResult loginResult = responseService.getLoginResult(user);

        loginResult.setToken(jwtTokenProvider.createToken(String.valueOf(user.getUserId()),user.getRoles()));

        return loginResult;
    }



/*
    @GetMapping("/user/{cardId}/check")
    public SingleResult<Boolean> checkPartnerLike(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());



        return null;
    }

*/




}
