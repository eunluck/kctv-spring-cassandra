package com.kctv.api.controller.v1;

import com.kctv.api.advice.exception.CUserNotFoundException;
import com.kctv.api.model.log.AppClkLogEntitiy;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.service.LoggingService;
import com.kctv.api.service.ResponseService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
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
    public ListResult<AppClkLogEntitiy> getListByUserId(@ApiParam(value = "uuid",required = true,example = "4df4e39e-0f50-409f-9e81-14bf37189706")@PathVariable("uuid") UUID uuid){

        return responseService.getListResult(loggingService.findByUserId(uuid));
    }

    @ApiOperation(value = "클릭 로그 저장", notes = "매일 클라이언트에서 보내주는 클릭 로그를 서버에 저장한다. ")
    @PostMapping("/clk")
    public CommonResult saveClkLog(@RequestBody List<AppClkLogEntitiy> clkLog){

        clkLog.forEach(appClkLogEntitiy -> Optional.ofNullable(loggingService.saveClkLog(appClkLogEntitiy)).orElseThrow(CUserNotFoundException::new));

    return responseService.getSuccessResult();

    }




}