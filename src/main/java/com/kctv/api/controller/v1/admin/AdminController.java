package com.kctv.api.controller.v1.admin;

import com.datastax.oss.driver.shaded.guava.common.base.Preconditions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kctv.api.advice.exception.CFormatNotAllowedException;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.advice.exception.CUserExistException;
import com.kctv.api.model.admin.FaqRequest;
import com.kctv.api.model.admin.FaqTableEntity;
import com.kctv.api.model.admin.QnaAnswerEntity;
import com.kctv.api.model.interview.InterviewContent;
import com.kctv.api.model.interview.OwnerInterviewEntity;
import com.kctv.api.model.place.PlaceInfoEntity;
import com.kctv.api.model.place.PlaceInfoDto;
import com.kctv.api.model.place.PlaceInfoVo;
import com.kctv.api.model.qna.QnaByUserEntity;
import com.kctv.api.model.qna.QnaDto;
import com.kctv.api.model.stylecard.PartnersByTags;
import com.kctv.api.model.stylecard.StyleCardInfoEntity;
import com.kctv.api.model.admin.stylecard.StyleCardDto;
import com.kctv.api.model.admin.stylecard.StyleCardVo;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.*;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;



@Api(tags = {"11. Admin API"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/admin")
public class AdminController {


    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ResponseService responseService;
    private final PlaceService placeService;
    private final StyleCardService styleCardService;
    private final StorageService storageService;
    private final FaqService faqService;
    private final QnaService qnaService;



    @ApiOperation(value = "전체 Place 목록 출력", notes = "등록된 모든 장소를 조회한다.")
    @GetMapping("/places")
    public ListResult<PlaceInfoEntity> getPlaceAll(){

        return responseService.getListResult(placeService.getPartnerInfoListService());

    }


    @ApiOperation(value = "신규 카드 추가", notes = "카드를 생성한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "요청 본문", dataType = "String", paramType = "form", defaultValue = "{     \"title\": \"브라이언의 주말여행\",     \"ages\":[\"20대\",\"30대\",\"40대\"],     \"tags\": [         \"따뜻한\",         \"제주여행\",         \"가족\",         \"디자이너\"     ],     \"gender\":[\"남\",\"여\"],     \"placeId\": [\"8196a739-6a56-4d11-aced-22950a4a6bfa\",\"0c888459-3a05-4396-afc6-83ba60b4908c\",\"b3dc4146-2827-47c0-bbc8-9fe350d4134e\"],     \"status\":\"MD추천\" }"),
            @ApiImplicitParam(name = "file", value = "이미지(여러개)", dataType = "file", paramType = "form"),
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping(value = "/card", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SingleResult<StyleCardDto> createCard(@RequestPart String request,
                                                  @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {

        StyleCardVo cardVoRequest = new ObjectMapper().readValue(request, StyleCardVo.class);

        StyleCardInfoEntity card = styleCardService.createStyleCard(cardVoRequest);

        if (file != null) {
            try {
                String resultPath = storageService.saveImage(card.getCardId(), file, "card");
                card.setCoverImage(resultPath);
            } catch (IOException e) {
                logger.warn("파일 첨부 에러 발생");
                styleCardService.deleteStyleCard(card);
                throw new CFormatNotAllowedException("이미지 첨부 중 오류 발생");
            }
        }

        return responseService.getSingleResult(new StyleCardDto(card, placeService.getPlaceListByIdIn(new ArrayList<>(card.getPlaceId()))));
    }


    @ApiOperation(value = "신규 장소 추가", notes = "신규 장소를 추가한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "file", value = "이미지(여러개)", dataType = "file", paramType = "form"),
            @ApiImplicitParam(name = "request", value = "요청 본문", dataType = "String", paramType = "form", defaultValue = "{\n" +
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
    @PostMapping(value = "/place", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SingleResult<PlaceInfoEntity> createPlace(@ApiIgnore @RequestPart String request,
                                                     @ApiIgnore @RequestPart(value = "file", required = false) List<MultipartFile> file) throws JsonProcessingException {

        PlaceInfoVo placeRequest = new ObjectMapper().readValue(request, PlaceInfoVo.class);
        PlaceInfoEntity place = null;

        try {
        place = placeService.createPlace(new PlaceInfoEntity(placeRequest), placeRequest.getMenuList());

        if (CollectionUtils.isNotEmpty(file)) {
            try {
                List<String> afterPath = storageService.saveImage(place.getPartnerId(), file, "place");
                place.setImages(afterPath);
            } catch (IOException e) {
                logger.warn("파일 첨부 에러 발생");
                placeService.deletePlace(place);
                throw new CFormatNotAllowedException("장소 등록 중 에러가 발생했습니다. 다시 시도해주세요.");
            }
        }
        }catch (Exception e){
            if (place != null)
            placeService.deletePlace(place);

        }
        return responseService.getSingleResult(place);
    }


    @ApiOperation(value = "장소 정보 수정", notes = "{   deleteImg에는 삭제할 이미지 주소를 리스트로 넣어준다.(ex:\"deleteImg\":[\"/image/797fad35-b079-4311-98f1-4a9798cca3fb\"] ) /n"+
            "\"title\": \"브라이언의 주말여행\",     \"ages\":[\"20대\",\"30대\",\"40대\"],     \"tags\": [         \"따뜻한\",         \"제주여행\",         \"가족\",         \"디자이너\"     ],     \"gender\":[\"남\",\"여\"],     \"placeId\": [\"8196a739-6a56-4d11-aced-22950a4a6bfa\",\"0c888459-3a05-4396-afc6-83ba60b4908c\",\"b3dc4146-2827-47c0-bbc8-9fe350d4134e\"],     \"status\":\"MD추천\" }")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "request", value = "요청 본문", dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "file", value = "커버 이미지", dataType = "file", paramType = "form")
    })
    @PutMapping(value = "/place/{placeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public SingleResult<PlaceInfoDto> modifyPlace(@PathVariable("placeId") UUID placeId,
                                                  @ApiIgnore @RequestPart String request,
                                                  @ApiIgnore @RequestPart(value = "file", required = false) List<MultipartFile> file) throws IOException {

        PlaceInfoVo placeRequest = new ObjectMapper().readValue(request, PlaceInfoVo.class);

        PlaceInfoEntity beforePlace = placeService.getPartnerByIdService(placeId).orElseThrow(CResourceNotExistException::new);

        List<String> emptyImgList;

        try {

            if ((CollectionUtils.isEmpty(placeRequest.getDeleteImg())) && file == null){

                PlaceInfoDto afterPlace = Optional.ofNullable(placeService.modifyPlace(new PlaceInfoEntity(placeRequest),beforePlace, placeRequest.getMenuList())).orElseThrow(CResourceNotExistException::new);

                return responseService.getSingleResult(afterPlace);

                //기존 파일에 대한 수정도 없고, 파일 추가도 없는경우. (완)

            }else if ((CollectionUtils.isEmpty(placeRequest.getDeleteImg())) && file != null){
                storageService.checkImgType(file);

                emptyImgList = beforePlace.getImages();
                if (emptyImgList == null){
                    emptyImgList = Lists.newArrayList();
                }

                for (MultipartFile appendFile : file) {
                    emptyImgList.add(storageService.appendPlaceImage(beforePlace, appendFile));
                }

                PlaceInfoDto afterPlace = Optional.ofNullable(placeService.modifyPlace(new PlaceInfoEntity(placeRequest,emptyImgList),beforePlace, placeRequest.getMenuList())).orElseThrow(CResourceNotExistException::new);

                return responseService.getSingleResult(afterPlace);

                //추가되는 파일만 있는경우.
                // -> 새로운 파일 저장 후 나온 path를 beforePlace에 ++

            }else if ((CollectionUtils.isNotEmpty(placeRequest.getDeleteImg())) && file == null){

                List<UUID> deleteImg = placeRequest.getDeleteImg().stream().map(s -> UUID.fromString(storageService.getImgIdByImgUrl(s))).collect(Collectors.toList());
                for (UUID imgId : deleteImg){
                    if(!storageService.removePlaceImage(imgId)){
                     throw new CResourceNotExistException("파일 삭제 중 에러 발생");
                    }
                }
                emptyImgList = beforePlace.getImages().stream().filter(s -> !placeRequest.getDeleteImg().contains(s)).collect(Collectors.toList());

                PlaceInfoDto afterPlace = Optional.ofNullable(placeService.modifyPlace(new PlaceInfoEntity(placeRequest,emptyImgList),beforePlace, placeRequest.getMenuList())).orElseThrow(CResourceNotExistException::new);

                return responseService.getSingleResult(afterPlace);
                //이미지 삭제만 있는경우
                // -> beforePlace.img - placeRequest.getDeleteImg
                // -> 이미지삭제 로직

            }else if ((CollectionUtils.isNotEmpty(placeRequest.getDeleteImg())) && file != null){

                //이미지 삭제와 새로운 이미지 추가가 같이 있는 경우
                storageService.checkImgType(file);

                List<UUID> deleteImg = placeRequest.getDeleteImg().stream().map(s -> UUID.fromString(storageService.getImgIdByImgUrl(s))).collect(Collectors.toList());
                for (UUID imgId : deleteImg){
                    if(!storageService.removePlaceImage(imgId)){
                        throw new CResourceNotExistException("파일 삭제 중 에러 발생");
                    }
                }
                emptyImgList = beforePlace.getImages().stream().filter(s -> !placeRequest.getDeleteImg().contains(s)).collect(Collectors.toList());

                for (MultipartFile appendFile : file) {
                    emptyImgList.add(storageService.appendPlaceImage(beforePlace, appendFile));
                }

                PlaceInfoDto afterPlace = Optional.ofNullable(placeService.modifyPlace(new PlaceInfoEntity(placeRequest,emptyImgList),beforePlace, placeRequest.getMenuList())).orElseThrow(CResourceNotExistException::new);

                return responseService.getSingleResult(afterPlace);

            } else {
                throw new CFormatNotAllowedException("수정 중 오류가 발생했습니다. 다시 시도해주세요.");
            }
        }catch (Exception e){
            throw new CFormatNotAllowedException("수정 중 오류가 발생했습니다. 다시 시도해주세요.");
        }
        //기존

     /*   if (file == null || file.isEmpty()) {
            PlaceInfoDto afterPlace = Optional.ofNullable(placeService.modifyPlace(new PlaceInfo(placeRequest),beforePlace, placeRequest.getMenuList())).orElseThrow(CResourceNotExistException::new);

            return responseService.getSingleResult(afterPlace);
        } else {
            for (MultipartFile multipartFile : file) {
                storageService.checkImgType(multipartFile);
            }

            List<UUID> beforeImgList = beforePlace.getImages()
                    .stream()
                    .map(s -> UUID.fromString(storageService.getImgIdByImgUrl(s)))
                    .collect(Collectors.toList());

            // 두 리스트가 같다 = 이미지 수정이 없었다.
            // beforeImgList가 더 많다 = 기존 이미지가 삭제되었다.


            *//*
            - 기존의 파일을 삭제한다.
                기존 파일 중 삭제 request
            - 기존의 파일을 두고 새로운 파일을 추가한다.
                기존파일 + 추가 request
            - 기존의 파일을 삭제하고 파일을 추가한다.
                삭제 request AND 추가 request
            *//*

            if (storageService.deleteFile(beforeImgList)) {
                try {
                    List<String> resultList = storageService.saveImage(beforePlace.getPartnerId(), file, "place");
                    beforePlace.setImages(resultList);
                } catch (IOException e) {
                    logger.warn("파일 첨부 에러 발생");
                }
                PlaceInfoDto afterPlace = Optional.ofNullable(placeService.modifyPlace(new PlaceInfo(placeRequest,beforePlace.getImages()),beforePlace, placeRequest.getMenuList())).orElseThrow(CResourceNotExistException::new);

                return responseService.getSingleResult(afterPlace);

            } else {
                throw new IOException("장소 수정 중 파일 첨부 에러 발생");
            }
        }*/
    }

    @ApiOperation(value = "스타일 카드 수정", notes = "등록된 스타일카드를 수정한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "request", value = "요청 본문", dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "file", value = "커버 이미지", dataType = "file", paramType = "form")
    })
    @PutMapping(value = "/card/{cardId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public SingleResult<StyleCardDto> modifyStyleCard(@ApiIgnore @RequestPart String request,
                                                       @PathVariable("cardId") UUID cardId,
                                                       @ApiIgnore @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        StyleCardInfoEntity cardRequest = new ObjectMapper().readValue(request, StyleCardInfoEntity.class);

        if (file == null || file.isEmpty()) {
            StyleCardInfoEntity afterPlace = styleCardService.modifyStyleCard(cardId, cardRequest);


            return responseService.getSingleResult(new StyleCardDto(afterPlace, placeService.getPlaceListByIdIn(new ArrayList<>(afterPlace.getPlaceId()))));
        } else {
            storageService.checkImgType(file);
            StyleCardInfoEntity beforeCard = Optional.ofNullable(styleCardService.getCardById(cardId)).orElseThrow(CResourceNotExistException::new);

            if (storageService.deleteFile(UUID.fromString(storageService.getImgIdByImgUrl(beforeCard.getCoverImage())))) {
                try {
                    String resultPath = storageService.saveImage(beforeCard.getCardId(), file, "card");
                    cardRequest.setCoverImage(resultPath);
                } catch (IOException e) {
                    logger.warn("파일 첨부 에러 발생");
                }
                StyleCardInfoEntity afterStyleCard = Optional.ofNullable(styleCardService.modifyStyleCard(beforeCard.getCardId(), cardRequest)).orElseThrow(CResourceNotExistException::new);



                return responseService.getSingleResult(new StyleCardDto(afterStyleCard, placeService.getPlaceListByIdIn(new ArrayList<>(afterStyleCard.getPlaceId()))));
            } else {
                throw new IOException("카드 수정 중 파일 첨부 에러 발생");
            }
        }
    }

/*

    @ApiOperation(value = "데이터정규화용 api", notes = "데이터정규화")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping(value = "/place/admin/tags/reset")
    public void resetTags() {

       styleCardService.addAllTagsTest();

    }
*/



    @ApiOperation(value = "장소가 속한 스타일카드 조회", notes = "장소가 속한 스타일카드 리스트를 조회한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping(value = "/place/{placeId}/card")
    public ListResult<StyleCardInfoEntity> placeByStyleCard(@PathVariable("placeId") UUID placeUuid) {

        List<StyleCardInfoEntity> searchResult = styleCardService.getStyleCardListAllService()
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
    public SingleResult<FaqTableEntity> createFaq(@RequestBody FaqRequest request) {

        return responseService.getSingleResult(faqService.postFaq(request.getQuestion(), request.getAnswer()));
    }


    @ApiOperation(value = "FAQ 수정", notes = "faq를 수정한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @PutMapping(value = "/faq")
    public SingleResult<FaqTableEntity> modifyFaq(@RequestBody FaqTableEntity faqTableEntity) {

        FaqTableEntity beforeFaq = faqService.findById(faqTableEntity.getFaqId());
        beforeFaq.setAnswer(faqTableEntity.getAnswer());
        beforeFaq.setModifyDt(new Date());
        beforeFaq.setQuestion(faqTableEntity.getQuestion());

        return responseService.getSingleResult(faqService.faqSaveOrUpdate(beforeFaq));
    }


    @ApiOperation(value = "FAQ 삭제", notes = "faq를 삭제한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @DeleteMapping(value = "/faq/{uuid}")
    public CommonResult deleteFaq(@PathVariable("uuid") UUID uuid) {

        faqService.deleteFaq(Optional.ofNullable(faqService.findById(uuid)).orElseThrow(CResourceNotExistException::new));

        return responseService.getSuccessResult();
    }




    @ApiOperation(value = "QNA 목록 조회", notes = "QNA를 조회한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping(value = "/qna")
    public ListResult<QnaByUserEntity> AdminQnaList(@RequestParam(value = "status", required = false) String status,
                                                    @RequestParam(value = "type", required = false) String type) {

        //TODO 하드코딩수정필요

        String typeSetter = "";
        if ("as".equals(type)) {
            typeSetter = "WiFi A/S";
        } else if ("zone".equals(type)) {
            typeSetter = "WiFi Zone";
        } else if ("app".equals(type)) {
            typeSetter = "WakeUf 앱 문의";
        } else if ("etc".equals(type)) {
            typeSetter = "기타 문의";
        }else if ("payment".equals(type)) {
            typeSetter = "결제 문의";
        }else if ("pay".equals(type)) {
            typeSetter = "결제 문의";
        } else {
            typeSetter = "기타 문의";
        }


        if (!"".equals(typeSetter))  {

            String finalTypeSetter = typeSetter;
            return responseService.getListResult(qnaService.getAllQnaList(status)
                    .stream()
                    .filter(qnaByUserEntity ->
                            qnaByUserEntity.getQuestionType().equals(finalTypeSetter))
                    .collect(Collectors.toList()));

        } else {
            return responseService.getListResult(qnaService.getAllQnaList(status));
        }
    }


    @ApiOperation(value = "QNA 답변등록", notes = "답변을 등록한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping(value = "/qna/{questionId}/answer")
    public CommonResult adminAnswer(@PathVariable("questionId")UUID questionId, @RequestBody QnaAnswerEntity qnaAnswerEntity) {

        qnaAnswerEntity.setQuestionId(questionId);
        qnaAnswerEntity.setCreateDt(new Date());
        qnaAnswerEntity.setAnswerId(com.datastax.oss.driver.api.core.uuid.Uuids.timeBased());

        List<QnaAnswerEntity> qnaAnswerEntities = qnaService.qnaByQuestionIdList(qnaAnswerEntity.getQuestionId());

        if (qnaAnswerEntities.size() > 1){
            return responseService.getFailResult(-99,"이미 등록된 답변이 존재합니다.");
        }

        QnaByUserEntity qnaUser = qnaService.findQuestion(qnaAnswerEntity.getQuestionId());

        qnaAnswerEntity.setUserId(qnaUser.getUserId());
        qnaService.createAnswer(qnaAnswerEntity);

        if (!Strings.isNullOrEmpty(qnaAnswerEntity.getStatus())){
            qnaUser.setStatus(qnaAnswerEntity.getStatus().replaceAll(" ",""));
            qnaService.modifyQuestion(qnaUser);
        }

        return responseService.getSuccessResult();

    }


    @ApiOperation(value = "QNA 처리상태 수정", notes = "질문의 처리상태만 수정한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @PutMapping(value = "/qna/{questionId}/status")
    public SingleResult<QnaDto> modifyAnswer(@PathVariable("questionId")UUID questionId,@RequestBody QnaByUserEntity qna) {

            QnaByUserEntity qnaByUserEntity = qnaService.findQuestion(questionId);
            qnaByUserEntity.setStatus(qna.getStatus().replaceAll(" ",""));
            qnaService.modifyQuestion(qnaByUserEntity);

        return responseService.getSingleResult(qnaService.getQna(questionId));

    }

    
    

    @ApiOperation(value = "QNA 수정", notes = "답변을 수정한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
    })
    @PutMapping(value = "/qna/{questionId}/answer/{answerId}")
    public SingleResult<QnaDto> modifyAnswer(@PathVariable("questionId")UUID questionId, @PathVariable("answerId")UUID answerId,
                                             @RequestBody QnaAnswerEntity request) {
        QnaAnswerEntity beforeAnswer = qnaService.findAnswerById(questionId,answerId);
        beforeAnswer.modify(request.getContent(),request.getStatus().replaceAll(" ",""));

        if (!Strings.isNullOrEmpty(request.getStatus())){
            QnaByUserEntity qnaByUserEntity = qnaService.findQuestion(questionId);
            qnaByUserEntity.setStatus(request.getStatus());
            qnaService.modifyQuestion(qnaByUserEntity);
        }

        QnaAnswerEntity after = qnaService.saveOrInsertAnswer(beforeAnswer);

        return responseService.getSingleResult(qnaService.getQna(questionId));

    }



    @ApiOperation(value = "Admin 라이프스타일 관리 목록", notes = "라이프스타일 관리에 필요한 목록을 호출한다")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping(value = "/card")
    public ListResult<StyleCardDto> getCardList() {

        List<StyleCardInfoEntity> searchResult = styleCardService.getStyleCardListAllService();

        return responseService.getListResult(searchResult.stream()
                .map(styleCardInfo -> new StyleCardDto(styleCardInfo, placeService.getPlaceListByIdIn(new ArrayList<>(styleCardInfo.getPlaceId()))))
                .collect(Collectors.toList()));

    }

}
