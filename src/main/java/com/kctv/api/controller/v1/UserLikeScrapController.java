package com.kctv.api.controller.v1;


import com.kctv.api.entity.ap.PartnerInfo;
import com.kctv.api.entity.tag.StyleCardInfo;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.service.LikeScrapService;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Api(tags = {"03. UserLikeAndScrap API"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class UserLikeScrapController {

    private final ResponseService responseService;
    private final LikeScrapService likeScrapService;

    @ApiOperation(value = "이 가게 조아요", notes = "좋아요를 추가하거나 삭제한다. (좋아요:true, 좋아요취소:false)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @PutMapping("/user/{placeId}/like")
    public CommonResult userLikePlace(@ApiParam("가게 UUID")@PathVariable("placeId") UUID placeId){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());


        return responseService.getSingleResult(likeScrapService.userLikePartnerService(userId,placeId).isPresent());
    }


    @ApiOperation(value = "나의 좋아요 리스트", notes = "내가 좋아요 한 가게 리스트 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @GetMapping("/user/like")
    public ListResult<PartnerInfo> likeList(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());

        return responseService.getListResult(likeScrapService.likeList(userId));

    }

    @ApiOperation(value = "좋아요한 가게 ID리스트", notes = "내가 좋아요한 소상공인 ID 리스트 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @GetMapping("/user/like/uuid")
    public ListResult<UUID> likeIdList(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());


        List<PartnerInfo> list = likeScrapService.likeList(userId);

        return responseService.getListResult(list.stream().map(PartnerInfo::getPartnerId).collect(Collectors.toList()));

    }


    @ApiOperation(value = "카드 스크랩", notes = "카드를 스크랩한다. (스크랩:true, 스크랩취소:false)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @PutMapping("/user/{cardId}/scrap")
    public CommonResult userScrapCard(@ApiParam("스타일카드 UUID")@PathVariable("cardId") UUID cardId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());


        return responseService.getSingleResult(likeScrapService.userScrapCardService(userId,cardId).isPresent());

    }


    @ApiOperation(value = "나의 스크랩 리스트", notes = "내가 스크랩한 스타일카드 리스트 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @GetMapping("/user/scrap")
    public ListResult<StyleCardInfo> scrapList(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());


        return responseService.getListResult(likeScrapService.scrapList(userId));
    }

    @ApiOperation(value = "스크랩한 카드 ID리스트", notes = "내가 스크랩한 스타일카드 ID 리스트 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @GetMapping("/user/scrap/uuid")
    public ListResult<UUID> scrapIdList(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());



        List<StyleCardInfo> list = likeScrapService.scrapList(userId);

        return responseService.getListResult(list.stream().map(StyleCardInfo::getCardId).collect(Collectors.toList()));

    }


}
