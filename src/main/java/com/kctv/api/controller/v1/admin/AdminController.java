package com.kctv.api.controller.v1.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.advice.exception.CUserExistException;
import com.kctv.api.controller.v1.admin.captive.CaptiveRequest;
import com.kctv.api.entity.admin.FaqRequest;
import com.kctv.api.entity.admin.FaqTable;
import com.kctv.api.entity.admin.QnaAnswer;
import com.kctv.api.entity.place.MenuByPlace;
import com.kctv.api.entity.place.PlaceInfo;
import com.kctv.api.entity.place.PlaceInfoDto;
import com.kctv.api.entity.place.PlaceInfoVo;
import com.kctv.api.entity.qna.QnaByUserEntity;
import com.kctv.api.entity.stylecard.StyleCardInfo;
import com.kctv.api.entity.stylecard.Tag;
import com.kctv.api.entity.stylecard.admin.StyleCardVo;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.repository.qna.QnaAnswerRepository;
import com.kctv.api.service.*;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.supplyAsync;


@Api(tags = {"11. Admin API"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value =  "/v1/admin")
public class AdminController {


    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ResponseService responseService;
    private final PlaceService placeService;
    private final StyleCardService styleCardService;
    private final StorageService storageService;
    private final FaqService faqService;

    private final QnaService qnaService;


/*
    @ApiOperation(value = "장소 리스트 조회", notes = "Header에 토큰을 검사하여 유저리스트를 호출한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/place")
    public ListResult<PlaceInfo> getPlaceList(){

        CassandraPageRequest pageRequest = CassandraPageRequest.of(0,100); //TODO 페이징 차후 구현


        return responseService.getListResult(placeService.pageableFindAllBy(pageRequest).getContent());
    }*/





    @ApiOperation(value = "신규 카드 추가", notes = "카드를 생성한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request",value = "요청 본문", dataType = "String", paramType = "form",defaultValue = "{     \"title\": \"브라이언의 주말여행\",     \"ages\":[\"20대\",\"30대\",\"40대\"],     \"tags\": [         \"따뜻한\",         \"제주여행\",         \"가족\",         \"디자이너\"     ],     \"gender\":[\"남\",\"여\"],     \"placeId\": [\"8196a739-6a56-4d11-aced-22950a4a6bfa\",\"0c888459-3a05-4396-afc6-83ba60b4908c\",\"b3dc4146-2827-47c0-bbc8-9fe350d4134e\"],     \"status\":\"MD추천\" }"),
            @ApiImplicitParam(name = "file",value = "이미지(여러개)",dataType = "file", paramType = "form"),
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping(value = "/card",consumes = MediaType.MULTIPART_FORM_DATA_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public SingleResult<StyleCardInfo> createCard(@RequestPart String request,
                                                  @RequestPart(value = "file",required = false) MultipartFile file) throws JsonProcessingException {

        StyleCardVo cardVoRequest = new ObjectMapper().readValue(request,StyleCardVo.class);

        StyleCardInfo card = styleCardService.createStyleCard(cardVoRequest);

        try {
        storageService.saveImage(card.getCardId(),file,"card");
        }catch (IOException e){
            logger.warn("파일 첨부 에러 발생");
        }


        return responseService.getSingleResult(card);
    }



    @ApiOperation(value = "신규 장소 추가", notes = "신규 장소를 추가한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "file",value = "이미지(여러개)",dataType = "file", paramType = "form"),
            @ApiImplicitParam(name = "request", value = "요청 본문", dataType = "String",paramType = "form", defaultValue = "{\n" +
                    "    \"businessName\": \"맛순이뼈해장국\",\n" +
                    "    \"storeType\": \"음식점\",\n" +
                    "    \"tags\": [\n" +
                    "        \"따뜻한\",\n" +
                    "        \"제주생활\",\n" +
                    "        \"가족\",\n" +
                    "        \"금융\"\n" +
                    "    ],\n" +
                    "    \"telNumber\": \"01023523943\",\n" +
                    "    \"facilities\": [\n" +
                    "        \"주차장 완비\"\n" +
                    "    ],\n" +
                    "    \"partnerAddress\": \"제주시\",\n" +
                    "    \"menuList\":[{\"menuType\":\"메인메뉴\",\"menuName\":\"뼈해장국\",\"menuDescription\":\"진하고 푸짐한 맛있는 뼈해장국\",\"menuPrice\":6000}],\n" +
                    "    \"periods\": [\n" +
                    "        {\n" +
                    "            \"close\": {\n" +
                    "                \"day\": 1,\n" +
                    "                \"time\": \"2200\"\n" +
                    "            },\n" +
                    "            \"open\": {\n" +
                    "                \"day\": 1,\n" +
                    "                \"time\": \"0900\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"close\": {\n" +
                    "                \"day\": 2,\n" +
                    "                \"time\": \"2200\"\n" +
                    "            },\n" +
                    "            \"open\": {\n" +
                    "                \"day\": 2,\n" +
                    "                \"time\": \"0900\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"close\": {\n" +
                    "                \"day\": 3,\n" +
                    "                \"time\": \"2200\"\n" +
                    "            },\n" +
                    "            \"open\": {\n" +
                    "                \"day\": 3,\n" +
                    "                \"time\": \"0900\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"close\": {\n" +
                    "                \"day\": 4,\n" +
                    "                \"time\": \"2200\"\n" +
                    "            },\n" +
                    "            \"open\": {\n" +
                    "                \"day\": 4,\n" +
                    "                \"time\": \"0900\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"close\": {\n" +
                    "                \"day\": 5,\n" +
                    "                \"time\": \"2200\"\n" +
                    "            },\n" +
                    "            \"open\": {\n" +
                    "                \"day\": 5,\n" +
                    "                \"time\": \"0900\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"close\": {\n" +
                    "                \"day\": 6,\n" +
                    "                \"time\": \"2200\"\n" +
                    "            },\n" +
                    "            \"open\": {\n" +
                    "                \"day\": 6,\n" +
                    "                \"time\": \"0900\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"close\": {\n" +
                    "                \"day\": 7,\n" +
                    "                \"time\": \"2200\"\n" +
                    "            },\n" +
                    "            \"open\": {\n" +
                    "                \"day\": 7,\n" +
                    "                \"time\": \"0900\"\n" +
                    "            }\n" +
                    "        }\n" +
                    "    \n" +
                    "    ]\n" +
                    "}")
    })
    @PostMapping(value = "/place",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SingleResult<PlaceInfo> createPlace(@ApiIgnore @RequestPart String request,
                                               @ApiIgnore @RequestPart(value = "file",required = false) List<MultipartFile> file) throws JsonProcessingException {


        System.out.println(request);
        PlaceInfoVo placeRequest = new ObjectMapper().readValue(request,PlaceInfoVo.class);

        PlaceInfo place = placeService.createPlace(new PlaceInfo(placeRequest),placeRequest.getMenuList());

        try {
            storageService.saveImage(place.getPartnerId(),file,"place");
        }catch (IOException e){
            logger.warn("파일 첨부 에러 발생");
        }


        return responseService.getSingleResult(place);
    }
    @ApiOperation(value = "장소 정보 수정", notes = "등록된 장소를 수정한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @PutMapping(value = "/place")
    public SingleResult<PlaceInfo> modifyPlace(@ApiIgnore @RequestPart String request,
                                               @ApiIgnore @RequestPart(value = "file",required = false) List<MultipartFile> file) throws JsonProcessingException {

        PlaceInfo placeRequest = new ObjectMapper().readValue(request,PlaceInfo.class);

        PlaceInfo afterPlace = Optional.ofNullable(placeService.modifyPlace(placeRequest)).orElseThrow(CResourceNotExistException::new);


        //TODO 파일 수정 로직 필요
        return responseService.getSingleResult(afterPlace);
    }

    @ApiOperation(value = "장소가 속한 스타일카드 조회", notes = "장소가 속한 스타일카드 리스트를 조회한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping(value = "/place/{placeId}/card")
    public ListResult<StyleCardInfo> placeByStyleCard(@PathVariable("placeId")UUID placeUuid){

        List<StyleCardInfo> searchResult = styleCardService.getStyleCardListAllService()
                .stream()
                .filter(styleCardInfo -> CollectionUtils.isNotEmpty(styleCardInfo.getPlaceId()))
                .filter(styleCardInfo -> styleCardInfo.getPlaceId().contains(placeUuid))
                .collect(Collectors.toList());




        return responseService.getListResult(searchResult);

    }


    @ApiOperation(value = "FAQ 등록", notes = "faq를 등록한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping(value = "/faq")
    public SingleResult<FaqTable> createFaq(@RequestBody FaqRequest request){

        return responseService.getSingleResult(faqService.postFaq(request.getQuestion(),request.getAnswer()));
    }

    @ApiOperation(value = "FAQ 수정", notes = "faq를 수정한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @PutMapping(value = "/faq")
    public SingleResult<FaqTable> modifyFaq(@RequestBody FaqTable faqTable){

        FaqTable beforeFaq = faqService.findById(faqTable.getFaqId());
        beforeFaq.setAnswer(faqTable.getAnswer());
        beforeFaq.setModifyDt(new Date());
        beforeFaq.setQuestion(faqTable.getQuestion());

        return responseService.getSingleResult(faqService.faqSaveOrUpdate(beforeFaq));
    }


    @ApiOperation(value = "FAQ 삭제", notes = "faq를 삭제한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @DeleteMapping(value = "/faq/{uuid}")
    public CommonResult deleteFaq(@PathVariable("uuid") UUID uuid){

        faqService.deleteFaq(Optional.ofNullable(faqService.findById(uuid)).orElseThrow(CResourceNotExistException::new));

        return responseService.getSuccessResult();
    }



    @ApiOperation(value = "QNA 목록 조회", notes = "QNA를 조회한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping(value = "/qna")
    public ListResult<QnaByUserEntity> AdminQnaList(@RequestParam(value = "status",required = false) String status,
                                                    @RequestParam(value = "type",required = false)String type){


        //TODO 하드코딩수정필요

        String typeSetter = "";
        if("as".equals(type)){
            typeSetter = "WiFi A/S";
        }else if ("zone".equals(type)){
            typeSetter = "WiFi Zone";
        }else if ("app".equals(type)){
            typeSetter = "WakeUf 앱 문의";
        }else if ("etc".equals(type)){
            typeSetter = "기타 문의";
        }


        if(!"".equals(typeSetter)){

        String finalTypeSetter = typeSetter;
            return responseService.getListResult(qnaService.getAllQnaList(status)
                    .stream()
                    .filter(qnaByUserEntity ->
                            qnaByUserEntity.getQuestionType().equals(finalTypeSetter))
                    .collect(Collectors.toList()));

        }else{
            return responseService.getListResult(qnaService.getAllQnaList(status));
        }



    }




    @ApiOperation(value = "QNA 답변등록", notes = "답변을 등록한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping(value = "/qna")
    public CommonResult adminAnswer(@RequestBody QnaAnswer qnaAnswer){



        qnaAnswer.setCreateDt(new Date());
        qnaAnswer.setAnswerId(com.datastax.oss.driver.api.core.uuid.Uuids.timeBased());

        QnaByUserEntity qnaUser = qnaService.findQuestion(qnaAnswer.getQuestionId());

        qnaUser.setStatus("답변완료");
        qnaUser.setRemark(qnaAnswer.getRemark());
        qnaAnswer.setUserId(qnaUser.getUserId());
        qnaService.createAnswer(qnaAnswer);
        qnaService.modifyQuestion(qnaUser);


        return responseService.getSuccessResult();

    }




    @ApiOperation(value = "캡티브 포탈 관리", notes = "캡티브포탈이미지를 등록한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping(value = "/cative/ad")
    public CommonResult captiveImgPost(@RequestPart CaptiveRequest request){

        MultipartFile file = request.getImgFile();


        // save(파일저장)
        // 디비저장









        return responseService.getSuccessResult();

    }





}
