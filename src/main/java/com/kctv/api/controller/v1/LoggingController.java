package com.kctv.api.controller.v1;

import com.kctv.api.advice.exception.CIncorrectPasswordException;
import com.kctv.api.advice.exception.CNotFoundEmailException;
import com.kctv.api.advice.exception.CUserExistException;
import com.kctv.api.advice.exception.CUserNotFoundException;
import com.kctv.api.config.security.JwtTokenProvider;
import com.kctv.api.entity.log.AppClkLog;
import com.kctv.api.entity.user.UserInfo;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.model.response.LoginResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.LoggingService;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;



@Api(tags = {"00. Log API"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class LoggingController {

    private final ResponseService responseService;
    private final LoggingService loggingService;

    @ApiOperation(value = "클릭 로그 조회", notes = "특정 유저의 UUID로 클릭 로그를 조회한다.")
    @GetMapping("/clk/{uuid}")
    public ListResult<AppClkLog> getListByUserId(@ApiParam(value = "uuid",required = true,example = "4df4e39e-0f50-409f-9e81-14bf37189706")@PathVariable("uuid") UUID uuid){

        return responseService.getListResult(loggingService.findByUserId(uuid));
    }

    @ApiOperation(value = "클릭 로그 저장", notes = "매일 클라이언트에서 보내주는 클릭 로그를 서버에 저장한다. ")
    @PostMapping("/clk")
    public CommonResult saveClkLog(@RequestBody List<AppClkLog> clkLog){

        clkLog.forEach(appClkLog -> Optional.ofNullable(loggingService.saveClkLog(appClkLog)).orElseThrow(CUserNotFoundException::new));

    return responseService.getSuccessResult();

    }




}