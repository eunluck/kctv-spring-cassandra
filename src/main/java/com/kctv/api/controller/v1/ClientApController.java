package com.kctv.api.controller.v1;


import com.kctv.api.model.ap.FindApRequest;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.WakeupPermissionService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Api(tags = {"13. Client AP API"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class ClientApController {



    private final ResponseService responseService;
    private final WakeupPermissionService wakeupPermissionService;



    @ApiOperation(value = "MAC주소 저장하기", notes = "KCTV Wifi를 연결한 상태로 어플 실행 시 bssid, client IP를 통해 Mac주소를 검색한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @GetMapping("/ap/mac")
    public CommonResult findMac(@ApiParam @RequestBody FindApRequest findApRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UUID uuid = UUID.fromString(authentication.getName());

        findApRequest.setUserId(uuid);




        return responseService.getSingleResult(wakeupPermissionService.saveUserIdToWakeUfPermission(findApRequest));
    }


}
