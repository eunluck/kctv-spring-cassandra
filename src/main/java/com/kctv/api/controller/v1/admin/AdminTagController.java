package com.kctv.api.controller.v1.admin;


import com.kctv.api.advice.exception.CCommunicationException;
import com.kctv.api.advice.exception.CUserExistException;
import com.kctv.api.entity.place.PlaceTypeEntity;
import com.kctv.api.entity.stylecard.Tag;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.PlaceService;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.StyleCardService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Api(tags = {"14. Admin Tag And PlaceType API"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value =  "/v1/admin")
public class AdminTagController {


    private final ResponseService responseService;
    private final PlaceService placeService;
    private final StyleCardService styleCardService;

    @ApiOperation(value = "업종 검색", notes = "부모 업종 타입을 입력하여 소속된 업종들을 검색한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/place/type/{parentType}/find")
    public SingleResult<?> getListTags(@ApiParam(value = "검색 파라미터", defaultValue = "사업장") @PathVariable("parentType") String tagType){


        return responseService.getSingleResult(placeService.getTagList(tagType));
    }

    @ApiOperation(value = "모든 업종 조회", notes = "모든 업종 타입을 조회한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/place/type")
    public SingleResult<?> getListTagsAll(){


        return responseService.getSingleResult(placeService.getTagListAllService());
    }


    @ApiOperation(value = "신규 업종 추가", notes = "신규 업종을 추가한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping(value = "/place/type")
    public CommonResult createPlaceType(@RequestBody PlaceTypeEntity placeTypeEntity){

        if (placeService.getTagOneService(placeTypeEntity).isPresent())
            return responseService.getFailResult(200,"이미 존재하는 태그입니다.");
        else
            return responseService.getSingleResult(placeService.createTagService(placeTypeEntity).orElseThrow(CUserExistException::new));
    }



    //tag가 중복되는지 단건 검사.
    @ApiOperation(value = "태그 중복 검사(true = 중복, false = 비중복)",notes = "이미 존재하는 태그인지 체크한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/place/type/{parentType}/{placeType}")
    public SingleResult<Boolean> placeTypeCheck(@PathVariable("parentType")String parentType,
                                          @PathVariable("placeType") String placeType){

        PlaceTypeEntity placeTypeEntity = PlaceTypeEntity.builder().placeParentType(parentType).placeType(placeType).build();
        Optional<PlaceTypeEntity> result = placeService.getTagOneService(placeTypeEntity);

        return responseService.getSingleResult(result.isPresent());


    }

    @ApiOperation(value = "태그 삭제", notes = "태그를 삭제한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @DeleteMapping(value = "/place/type")
    public CommonResult deletePlaceType(@RequestBody PlaceTypeEntity placeTypeEntity){

        if(placeService.deleteTag(placeTypeEntity)){
            return responseService.getSuccessResult();
        }else {
            return responseService.getFailResult(-1,"이미 사용중이거나 중복되는 업종명입니다.");
        }
    }



    @ApiOperation(value = "신규 태그 추가", notes = "신규 태그를 추가한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping(value = "/keyword")
    public CommonResult createKeyword(@RequestBody Tag keyword){

        if (styleCardService.getTagOneService(keyword).isPresent())
            return responseService.getFailResult(200,"이미 존재하는 태그입니다.");
        else
            return responseService.getSingleResult(styleCardService.createTagService(keyword).orElseThrow(CUserExistException::new));
    }


    @ApiOperation(value = "태그 삭제", notes = "태그를 삭제한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @DeleteMapping(value = "/keyword")
    public CommonResult deleteKeyword(@RequestBody Tag keyword){


        if(styleCardService.deleteTag(keyword)){
            return responseService.getSuccessResult();
        }else {
            return responseService.getFailResult(-1,"이미 사용중이거나 중복되는 태그입니다.");
        }

    }


}
