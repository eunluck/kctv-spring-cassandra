package com.kctv.api.controller.v1;


import com.kctv.api.advice.exception.CNotOwnerException;
import com.kctv.api.model.interview.OwnerInterviewDto;
import com.kctv.api.model.interview.OwnerInterviewEntity;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.InterviewService;
import com.kctv.api.service.PlaceService;
import com.kctv.api.service.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Api(tags = {"015. OwnerInterview API"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class OwnerInterviewController {

    private final InterviewService interviewService;
    private final PlaceService placeService;
    private final ResponseService responseService;

    @ApiOperation(value = "사장님 이야기 목록", notes = "사장님이야기 목록을 호출한다.")
    @GetMapping("/place/interview")
    public ListResult<OwnerInterviewEntity> getOwnerInterviewList(){
        return responseService.getListResult(interviewService.findByOwnerInterviewListService());
    }

    @ApiOperation(value = "사장님 이야기 장소아이디로 상세보기", notes = "placeId를 통해 사장님이야기를 상세 조회한다.")
    @GetMapping("/place/{placeId}/interview")
    public CommonResult getOwnerInterviewByPlaceId(@PathVariable("placeId")UUID placeId){


            OwnerInterviewEntity ownerInterviewEntity = interviewService.findByOwnerInterviewEntityByPlaceId(placeId).orElseGet(() -> null);

            return ownerInterviewEntity != null ? responseService.getSingleResult(new OwnerInterviewDto(ownerInterviewEntity,placeService.getPartnerByIdService(placeId).get())) : responseService.getSingleResult(null);
        }




    @ApiOperation(value = "사장님 이야기 상세보기", notes = "interviewId 통해 사장님이야기를 상세 조회한다.")
    @GetMapping("/place/interview/{interviewId}")
    public SingleResult<OwnerInterviewDto> getOwnerInterviewByInterviewId(@PathVariable("interviewId")UUID interviewId){

        OwnerInterviewEntity ownerInterviewEntity = interviewService.findByInterviewId(interviewId);

        return responseService.getSingleResult(new OwnerInterviewDto(ownerInterviewEntity,placeService.getPartnerByIdService(ownerInterviewEntity.getPlaceId()).orElseThrow(CNotOwnerException::new)));

    }


}
