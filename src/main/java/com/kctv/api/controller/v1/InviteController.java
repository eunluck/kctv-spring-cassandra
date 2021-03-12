package com.kctv.api.controller.v1;

import com.kctv.api.model.user.InviteFriendsEntity;
import com.kctv.api.model.user.UserInfoEntity;
import com.kctv.api.model.ap.WakeupPermissionVO;
import com.kctv.api.model.request.ReferRequest;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.service.InviteService;
import com.kctv.api.service.ResponseService;
import io.swagger.annotations.*;
import org.bouncycastle.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Api(tags = {"15. invite API"})
@RestController
@RequestMapping("/v1/invite")

public class InviteController {

    private final ResponseService responseService;
    private final InviteService inviteService;
    private final String path;
    private final RestTemplate restTemplate;

    public InviteController(ResponseService responseService, InviteService inviteService, @Value("${costom.wakeuf.bryan.path}") String path, RestTemplateBuilder builder) {
        this.responseService = responseService;
        this.inviteService = inviteService;
        this.path = path;
        this.restTemplate = builder.build();
    }

    @ApiOperation(value = "추천인 코드 입력", notes = "추천인 코드를 입력하여 데이터를 보너스 데이터를 제공받는다. ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @PutMapping("/{friendCode}")
    public CommonResult acceptInvitation(@ApiParam(value = "추천인 코드",required = true, defaultValue = "8d0a10") @PathVariable("friendCode")String code){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID uuid = UUID.fromString(authentication.getName());

        String lowerCode = Strings.toLowerCase(code);


        Optional<UserInfoEntity> user = inviteService.findUserByCode(lowerCode);

        if (!user.isPresent()){
            return responseService.getFailResult(-11,"유효하지 않는 코드입니다.");
        }else if(inviteService.inviteDoubleCheck(uuid,user.get().getUserId())) {
            return responseService.getFailResult(-12,"이미 추천했던 사용자예요.");
        }else if(uuid.equals(user.get().getUserId())) {
            return responseService.getFailResult(-13,"자기 자신은 추천할 수 없어요");
        }else {

            InviteFriendsEntity inviteFriendsEntity = new InviteFriendsEntity(uuid,user.get().getUserId(),new Date());

            if (inviteService.saveInviteCode(inviteFriendsEntity,user.get())) {
                //추천인 코드는 여러명을 입력할 수 있다.(완)
                //한번 추천한 사람에게 중복 추천할 수 없다. (완)
                //추천인과 추천등록한사람은 100MB씩 제공받는다.(완)

                HttpEntity<ReferRequest> httpEntity = new HttpEntity<>(new ReferRequest(user.get().getUserId(),uuid,"104857600"));

                try {
                ResponseEntity<WakeupPermissionVO> responseEntity = restTemplate.exchange(path+"/wakeuf/refer",HttpMethod.POST,httpEntity, WakeupPermissionVO.class); // 브라이언 서버와 통신
                WakeupPermissionVO objects = responseEntity.getBody();

                return responseService.getSingleResult(objects);

                }catch (Exception e){
                    e.printStackTrace();
                    inviteService.deleteInviteCode(inviteFriendsEntity);
                    return responseService.getFailResult(-20,"Permission 통신 오류.");
                }

            }else{
                return responseService.getFailResult(-19,"친구 추천이 실패했습니다. 관리자에게 문의바랍니다.");
            }
        }
    }




}
