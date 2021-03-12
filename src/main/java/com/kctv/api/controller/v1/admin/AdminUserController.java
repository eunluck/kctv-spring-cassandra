package com.kctv.api.controller.v1.admin;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kctv.api.advice.exception.CNotFoundCodeException;
import com.kctv.api.advice.exception.CUserExistException;
import com.kctv.api.advice.exception.CUserNotFoundException;
import com.kctv.api.model.admin.AddManagerRequest;
import com.kctv.api.model.admin.AdminPaymentInfoEntity;
import com.kctv.api.model.admin.AdminPaymentInfoDto;
import com.kctv.api.model.admin.ManagerDto;
import com.kctv.api.model.ap.WakeupPermissionEntity;
import com.kctv.api.model.payment.PaymentCodeEntity;
import com.kctv.api.model.payment.PaymentInfoEntity;
import com.kctv.api.model.user.UserInfoEntity;
import com.kctv.api.model.user.UserInfoDto;
import com.kctv.api.model.user.UserInterestTagEntity;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.model.tag.Role;
import com.kctv.api.service.PaymentService;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.UserService;
import com.kctv.api.service.WakeupPermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Api(tags = {"12. Admin User API"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value =  "/v1/admin/user")
public class AdminUserController {

    private final ResponseService responseService;
    private final UserService userService;
    private final WakeupPermissionService wakeupPermissionService;
    private final PaymentService paymentService;



    @ApiOperation(value = "사용자용 내 정보 보기", notes = "토큰을 통해 로그인한 관리자의 정보를 확인한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/me")
    public SingleResult<ManagerDto> me() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID uuid = UUID.fromString(authentication.getName());
        UserInfoEntity user = Optional.of(userService.findByUserId(uuid)).orElseThrow(CUserNotFoundException::new);

        return responseService.getSingleResult(new ManagerDto(user));
    }



    @ApiOperation(value = "사용자, 관리자 권한 목록 조회", notes = "권한 목록을 조회한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/role")
    public ListResult<?> roleList() {


        return responseService.getListResult(Arrays.stream(Role.values()).collect(Collectors.toList()));
    }


    @ApiOperation(value = "관리자용 유저목록 조회", notes = "관리자용 유저리스트를 호출한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/list")
    public ListResult<UserInfoDto> getAllUser() {

        return responseService.getListResult(userService.getAllUserService().stream().map(userInfo ->
                new UserInfoDto(userInfo, new ArrayList<String>(userService.getUserInterestTag(userInfo.getUserId())
                        .orElseGet(() -> new UserInterestTagEntity(userInfo.getUserId(), null, Sets.newHashSet()))
                        .getTags()))).filter(userInfoDto -> !Role.adminIsTrue(userInfoDto.getRoles()))
                .collect(toList()));
    }


    @ApiOperation(value = "관리자용 관리자목록 조회", notes = "관리자용 유저리스트를 호출한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/list/manager")
    public ListResult<ManagerDto> getAdminList() {

        return responseService.getListResult(userService.getAllUserService().stream()
                .filter(userInfo -> Role.adminIsTrue(userInfo.getRoles()))
                .filter(userInfo ->  !userInfo.getRoles().contains("ROLE_TEMP_PASSWORD"))
                .map(ManagerDto::new)
                .collect(toList()));
    }




    @ApiOperation(value = "관리자용 유저 상세 조회", notes = "유저의 ID를 통해 상세 조회한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/{userId}")
    public SingleResult<UserInfoDto> getUserById(@PathVariable UUID userId) {

        List<PaymentCodeEntity> codeList = paymentService.findByCodeList();
        UserInfoEntity userInfoEntity = userService.findByUserId(userId);
        WakeupPermissionEntity wakeupPermissionEntity = wakeupPermissionService.findPermissionByUserId(userInfoEntity.getUserId());
        List<PaymentInfoEntity> paymentInfoEntity = paymentService.findByUserId(userInfoEntity.getUserId());

        if(paymentInfoEntity.size() != 0){
        List<AdminPaymentInfoEntity> result = paymentInfoEntity.stream()
                .filter(paymentInfoEntity1 -> ! "p001".equals(paymentInfoEntity1.getAppPaymentCode()))
                .filter(paymentInfoEntity1 -> ! "FREE".equals(paymentInfoEntity1.getAppPaymentCode()))
                .map(paymentInfoEntityStream ->
                codeList.stream()
                        .filter(paymentCodeEntity -> paymentCodeEntity.getAppPaymentCode().equals(paymentInfoEntityStream.getAppPaymentCode()))
                        .findFirst()
                        .map(paymentCodeEntityMap ->
                                AdminPaymentInfoEntity
                                        .builder()
                                        .paymentName(paymentCodeEntityMap.getDescription())
                                        .endDate(paymentInfoEntityStream.getEndDt())
                                        .paymentType(paymentInfoEntityStream.getSubscriptionPeriodAndroid())
                                        .price(paymentCodeEntityMap.getPrice())
                                        .referFriend(paymentInfoEntityStream.getFriendEmailReferMe())
                                        .startDate(paymentInfoEntityStream.getCreateDt())
                                        .status(paymentInfoEntityStream.getEndDt()==null?null:paymentInfoEntityStream.getEndDt().after(new Date())?"사용중":"만료")
                                        .build())
                        .orElseThrow(CNotFoundCodeException::new))
                .collect(Collectors.toList());

                //new AdminPaymentInfo(codeList.stream().filter(paymentCodeEntity -> paymentCodeEntity.equals(paymentInfoStream.getAppPaymentCode())).findFirst().map(PaymentCodeEntity::getDescription).orElseGet(null),codeList.stream().filter(paymentCodeEntity -> paymentCodeEntity.equals(paymentInfoStream.getAppPaymentCode())).findFirst().map(PaymentCodeEntity::getPrice).orElseGet(() -> 0L),paymentInfoStream.getCreateDt(),paymentInfoStream.getEndDt(),null,paymentInfoStream.getFriendEmailReferMe())).collect(Collectors.toList());
            return responseService.getSingleResult(new AdminPaymentInfoDto(userInfoEntity,wakeupPermissionEntity,result));
        } else {
            return responseService.getSingleResult(new AdminPaymentInfoDto(userInfoEntity,wakeupPermissionEntity, Lists.newArrayList()));

        }

        //return responseService.getSingleResult(new UserInfoDto(userService.findByUserId(userId)));
    }

/*
- 무선사업국: 관리자화면의 모든페이지
- 고객감동실: 회원관리(개인/소상공인), AP관리
- 기술국: AP관리
*/

    @ApiOperation(value = "관리자 계정을 추가한다.", notes = "관리자 계정을 추가한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping("/manager")
    public SingleResult<ManagerDto> createManagerUser(@RequestBody AddManagerRequest request) throws RoleNotFoundException {

        Optional<UserInfoEntity> requestUser = userService.checkByEmail(request.getManagerId(),"user");

        if (requestUser.isPresent()) {
            throw new CUserExistException();
        }

        return responseService.getSingleResult(new ManagerDto(userService.addManager(request.newAddManager(Role.findAuthorityByDescription(request.getRole())))));
    }



    @ApiOperation(value = "관리자 계정을 수정한다.", notes = "유저의 ID(UUID)를 통해 계정을 수정한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @PutMapping("/manager/{managerId}")
    public SingleResult<ManagerDto> modifyManagerUser(@PathVariable("managerId")UUID uuid,
                                                       @RequestBody AddManagerRequest request) throws RoleNotFoundException {

            request.setManagerId(null);
            UserInfoEntity requestUserInfoEntity = request.parseUserInfo(uuid);

        UserInfoEntity userInfoEntity = userService.modifyManager(requestUserInfoEntity);

        return responseService.getSingleResult(new ManagerDto(userInfoEntity));
    }


    @ApiOperation(value = "관리자 계정을 삭제한다.", notes = "유저의 ID(UUID)를 통해 계정을 삭제한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @DeleteMapping("/manager/{managerId}")
    public SingleResult<ManagerDto> deleteManagerUser(@PathVariable("managerId")UUID uuid) {


        UserInfoEntity userInfoEntity = Optional.of(userService.findByUserId(uuid)).orElseThrow(CUserNotFoundException::new);

        userService.deleteManager(userInfoEntity);

        return responseService.getSingleResult(new ManagerDto(userInfoEntity));
    }



}