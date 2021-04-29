package com.kctv.api.controller.v1;

import com.google.common.collect.Lists;
import com.kctv.api.model.qna.QnaByUserEntity;
import com.kctv.api.model.user.UserInfoEntity;
import com.kctv.api.model.qna.QnaDto;
import com.kctv.api.model.qna.QnaRequest;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.QnaService;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Api(tags = {"09.QnaAPI"})
@RestController
@AllArgsConstructor
@RequestMapping("/v1")
public class QnaController {


    private final UserService userService;
    private final QnaService qnaService;
    private final ResponseService responseService;




    @ApiOperation(value = "내가 등록한 QnA", notes = "등록했던 문의 글 목록을 조회한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @GetMapping("/qna/me")
    public ListResult<QnaDto> getMyQnaList(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoEntity user = (UserInfoEntity)authentication.getPrincipal();

        List<QnaByUserEntity> qnaList = qnaService.getQnaList(user.getUserId());
        List<QnaDto> result = Lists.newArrayList();

        for (QnaByUserEntity qna : qnaList){

            result.add(qnaService.getQna(qna.getQuestionId()));
        }



        return responseService.getListResult(result);
    }



    @ApiOperation(value = "QnA상세보기", notes = "등록했던 문의 글을 상세 조회한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @GetMapping("/qna/{questionId}")
    public SingleResult<QnaDto> getQnaByUserId(@PathVariable("questionId") UUID questionId){


        return responseService.getSingleResult(qnaService.getQna(questionId));
    }



    @ApiOperation(value = "QnA 질문 등록", notes = "질문을 등록한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),

    })
    @PostMapping("/qna")
    public SingleResult<QnaByUserEntity> postQnaByUser(@RequestBody QnaRequest qnaRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoEntity user = (UserInfoEntity)authentication.getPrincipal();


        return responseService.getSingleResult(qnaService.postQuestion(qnaRequest,user.getUserId(),user.getUserNickname(),user.getUserEmail()));
    }





}
