package com.kctv.api.controller.v1.admin;

import com.datastax.oss.driver.shaded.guava.common.base.Preconditions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kctv.api.advice.exception.CFormatNotAllowedException;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.model.admin.FaqRequest;
import com.kctv.api.model.admin.FaqTableEntity;
import com.kctv.api.model.admin.QnaAnswerEntity;
import com.kctv.api.model.interview.InterviewContent;
import com.kctv.api.model.interview.OwnerInterviewEntity;
import com.kctv.api.model.place.PlaceInfoEntity;
import com.kctv.api.model.place.PlaceInfoDto;
import com.kctv.api.model.place.PlaceInfoVo;
import com.kctv.api.model.qna.QnaByUserEntity;
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
    private final InterviewService interviewService;


    @ApiOperation(value = "사장님이야기 상태변경", notes = "사장님이야기의 상태를 변경한다.")
    @PutMapping(value = "/place/interview/{interviewId}/status")
    public SingleResult<OwnerInterviewEntity> modifyStatus(@PathVariable("interviewId")UUID interviewId){

        OwnerInterviewEntity ownerInterviewEntity = interviewService.findByInterviewId(interviewId);

        ownerInterviewEntity.setStatus(ownerInterviewEntity.getStatus() ? false : true);

        return responseService.getSingleResult(interviewService.saveOwnerInterviewEntity(ownerInterviewEntity));

    }

/*


    @ApiOperation(value = "사장님이야기 수정", notes = "장소 ID를 통해 사장님이야기 수정",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header")
    })
    @PutMapping(value = "/place/interview/{interviewId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SingleResult<OwnerInterviewEntity> modifyOwnerInterview(@PathVariable("interviewId")UUID interviewId,
                                                                 @RequestPart String request,
                                                                 @ApiIgnore @RequestParam Map<String,MultipartFile> image
                                                                 ) throws IOException {


        OwnerInterviewEntity ownerInterviewEntity = new ObjectMapper().readValue(request,OwnerInterviewEntity.class);

        ownerInterviewEntity.setModifyDt(new Date());

        //OwnerInterviewEntity beforeInterview = interviewService.findByInterviewId(interviewId);


        PlaceInfoEntity placeInfoEntity = placeService.getPartnerByIdService(placeId).orElseThrow(CResourceNotExistException::new);
        //ownerInterviewEntity.setPlaceInfo(placeInfoEntity);

        //인터뷰컨텐츠 객체중 키값이 'cover'라면 커버이미지로 판단

            image.entrySet()
                .stream()
                .filter(stringMultipartFileEntry -> "cover".equals(stringMultipartFileEntry.getKey()))
                    .findFirst()
                    .ifPresent(stringMultipartFileEntry ->
                            ownerInterviewEntity.setCoverImg("/place/"+placeInfoEntity.getPartnerId()+"/interview/"+ownerInterviewEntity.getInterviewId()+"/image/"+"cover"));


        List<InterviewContent> imageContent = image.entrySet()
                .stream()
                .filter(stringMultipartFileEntry -> !"cover".equals(stringMultipartFileEntry.getKey()))
                .map(stringMultipartFileEntry ->
                        new InterviewContent(Integer.parseInt(stringMultipartFileEntry.getKey()),"image","/place/"+placeInfoEntity.getPartnerId()+"/interview/"+ownerInterviewEntity.getInterviewId()+"/image/"+stringMultipartFileEntry.getKey()))
                .collect(Collectors.toList());

        for (InterviewContent interviewContent: imageContent){
        ownerInterviewEntity.getInterviewContents().add(interviewContent.getContentOrder(),interviewContent);
        }

        storageService.ownerInterviewUpload(placeInfoEntity.getPartnerId(),image,ownerInterviewEntity.getInterviewId()); // 파일저장

        interviewService.postOwnerInterviewService(ownerInterviewEntity);

        return null;

        //return responseService.getSingleResult(interviewService.postOwnerInterviewService());

    }
*/




    @ApiOperation(value = "사장님이야기 등록 step.1", notes = "장소 ID를 통해 사장님이야기 등록",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "title", value = "요청 본문", dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "ownerSaying", value = "요청 본문", dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "cover", value = "커버 이미지", dataType = "file", paramType = "form")
    })
    @PostMapping(value = "/place/{placeId}/interview/1",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SingleResult<OwnerInterviewEntity> postOwnerInterviewStep1(@PathVariable("placeId")UUID placeId,
                                                                      @RequestParam String title,
                                                                      @RequestParam String ownerSaying,
                                                                      @ApiIgnore @RequestPart MultipartFile cover
    ) throws IOException {

        //OwnerInterviewEntity ownerInterviewEntity = new ObjectMapper().readValue(request,OwnerInterviewEntity.class);
        OwnerInterviewEntity ownerInterviewEntity = new OwnerInterviewEntity();

        ownerInterviewEntity.setTitle(title);
        ownerInterviewEntity.setOwnerSaying(ownerSaying);
        ownerInterviewEntity.setPlaceId(placeId);
        ownerInterviewEntity.setInterviewId(UUID.randomUUID());
        ownerInterviewEntity.setCreateDt(new Date());
        ownerInterviewEntity.setStatus(false);


        PlaceInfoEntity placeInfoEntity = placeService.getPartnerByIdService(placeId).orElseThrow(CResourceNotExistException::new);
        //ownerInterviewEntity.setPlaceInfo(placeInfoEntity);

        //인터뷰컨텐츠 객체중 키값이 'cover'라면 커버이미지로 판단

        ownerInterviewEntity.setCoverImg("/place/"+placeInfoEntity.getPartnerId()+"/interview/"+ownerInterviewEntity.getInterviewId()+"/image/"+"cover");

        storageService.ownerInterviewUploadStepOne(placeInfoEntity.getPartnerId(),cover,ownerInterviewEntity.getInterviewId());
        OwnerInterviewEntity result = interviewService.postOwnerInterviewService(ownerInterviewEntity);

        return responseService.getSingleResult(result);

        //return responseService.getSingleResult(interviewService.postOwnerInterviewService());

    }




    @ApiOperation(value = "사장님이야기 수정 step.1", notes = "장소 ID를 통해 사장님이야기 수정",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "title", value = "요청 본문", dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "ownerSaying", value = "요청 본문", dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "cover", value = "커버 이미지", dataType = "file", paramType = "form")
    })
    @PutMapping(value = "/place/interview/{interviewId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SingleResult<OwnerInterviewEntity> modifyOwnerInterviewStep1(@PathVariable("interviewId")UUID interviewId,
                                                                      @RequestParam String title,
                                                                      @RequestParam String ownerSaying,
                                                                      @ApiIgnore @RequestPart MultipartFile cover
    ) throws IOException {

        //OwnerInterviewEntity ownerInterviewEntity = new ObjectMapper().readValue(request,OwnerInterviewEntity.class);

        OwnerInterviewEntity beforeEntity = interviewService.findByInterviewId(interviewId);
        OwnerInterviewEntity ownerInterviewEntity = new OwnerInterviewEntity();

        if (!Strings.isNullOrEmpty(title))
        beforeEntity.setTitle(title);
        if (!Strings.isNullOrEmpty(ownerSaying))
        beforeEntity.setOwnerSaying(ownerSaying);
        beforeEntity.setModifyDt(new Date());

        //ownerInterviewEntity.setPlaceInfo(placeInfoEntity);

        //인터뷰컨텐츠 객체중 키값이 'cover'라면 커버이미지로 판단

        if (cover != null){
        ownerInterviewEntity.setCoverImg("/place/"+beforeEntity.getPlaceId()+"/interview/"+ownerInterviewEntity.getInterviewId()+"/image/"+"cover");

        storageService.ownerInterviewModifyCover(beforeEntity.getPlaceId(),cover,ownerInterviewEntity.getInterviewId());

        }
        OwnerInterviewEntity result = interviewService.saveOwnerInterviewEntity(ownerInterviewEntity);

        return responseService.getSingleResult(result);

        //return responseService.getSingleResult(interviewService.postOwnerInterviewService());

    }



    @ApiOperation(value = "사장님이야기 수정 step.2", notes = "장소 ID를 통해 사장님이야기 수정",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "3", value = "파일테스트", dataType = "file", paramType = "form"),
            @ApiImplicitParam(name = "request", value = "파일테스트", dataType = "string", paramType = "form",defaultValue = "[   {     \"content\": \"컨텐츠제목\",     \"contentOrder\": 0,     \"contentType\": \"sub\"   },   {     \"content\": \"컨텐츠내용\",     \"contentOrder\": 1,     \"contentType\": \"text\"   },{     \"content\": \"컨텐츠내용둘\",     \"contentOrder\": 2,     \"contentType\": \"text\"   } ]")

    })
    @PutMapping(value = "/place/{placeId}/interview/{interviewId}/2",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SingleResult<OwnerInterviewEntity> modifyOwnerInterviewStep2(@PathVariable("placeId")UUID placeId,
                                                                      @PathVariable("interviewId")UUID interviewId,
                                                                      @ApiIgnore @RequestPart String request,
                                                                      @ApiIgnore @RequestParam Map<String,MultipartFile> image
    ) throws IOException {


        List<InterviewContent> interviewContents = new ObjectMapper().readValue(request, new TypeReference<List<InterviewContent>>() {});

        OwnerInterviewEntity ownerInterviewEntity = interviewService.findByInterviewId(interviewId);

        if (ownerInterviewEntity == null || !placeId.equals(ownerInterviewEntity.getPlaceId())){
            throw new CResourceNotExistException();
        }

        ownerInterviewEntity.setInterviewContents(interviewContents);
        ownerInterviewEntity.setModifyDt(new Date());

        PlaceInfoEntity placeInfoEntity = placeService.getPartnerByIdService(placeId).orElseThrow(CResourceNotExistException::new);
        //ownerInterviewEntity.setPlaceInfo(placeInfoEntity);

        //인터뷰컨텐츠 객체중 키값이 'cover'라면 커버이미지로 판단

        List<InterviewContent> imageContent = image.entrySet()
                .stream()
                .map(stringMultipartFileEntry ->
                        new InterviewContent(Integer.parseInt(stringMultipartFileEntry.getKey()),"image","/place/"+placeInfoEntity.getPartnerId()+"/interview/"+ownerInterviewEntity.getInterviewId()+"/image/"+stringMultipartFileEntry.getKey()))
                .collect(Collectors.toList());

        for (InterviewContent interviewContent: imageContent){
            ownerInterviewEntity.getInterviewContents().add(interviewContent.getContentOrder(),interviewContent);
        }

        storageService.ownerInterviewUploadStepTwo(placeInfoEntity.getPartnerId(),image,ownerInterviewEntity.getInterviewId()); // 파일저장

        return responseService.getSingleResult(interviewService.saveOwnerInterviewEntity(ownerInterviewEntity));

        //return responseService.getSingleResult(interviewService.postOwnerInterviewService());

    }



    @ApiOperation(value = "사장님이야기 등록 step.2", notes = "장소 ID를 통해 사장님이야기 등록",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "3", value = "파일테스트", dataType = "file", paramType = "form"),
            @ApiImplicitParam(name = "request", value = "파일테스트", dataType = "string", paramType = "form",defaultValue = "[   {     \"content\": \"컨텐츠제목\",     \"contentOrder\": 0,     \"contentType\": \"sub\"   },   {     \"content\": \"컨텐츠내용\",     \"contentOrder\": 1,     \"contentType\": \"text\"   },{     \"content\": \"컨텐츠내용둘\",     \"contentOrder\": 2,     \"contentType\": \"text\"   } ]")

    })
    @PostMapping(value = "/place/{placeId}/interview/{interviewId}/2",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SingleResult<OwnerInterviewEntity> postOwnerInterviewStep2(@PathVariable("placeId")UUID placeId,
                                                                      @PathVariable("interviewId")UUID interviewId,
                                                                      @ApiIgnore @RequestPart String request,
                                                                      @ApiIgnore @RequestParam Map<String,MultipartFile> image
    ) throws IOException {


        List<InterviewContent> interviewContents = new ObjectMapper().readValue(request, new TypeReference<List<InterviewContent>>() {});

        OwnerInterviewEntity ownerInterviewEntity = interviewService.findByInterviewId(interviewId);

        if (ownerInterviewEntity == null || !placeId.equals(ownerInterviewEntity.getPlaceId())){
            throw new CResourceNotExistException();
        }

        ownerInterviewEntity.setInterviewContents(interviewContents);
        ownerInterviewEntity.setCreateDt(new Date());
        ownerInterviewEntity.setStatus(true);

        PlaceInfoEntity placeInfoEntity = placeService.getPartnerByIdService(placeId).orElseThrow(CResourceNotExistException::new);
        //ownerInterviewEntity.setPlaceInfo(placeInfoEntity);

        //인터뷰컨텐츠 객체중 키값이 'cover'라면 커버이미지로 판단

        List<InterviewContent> imageContent = image.entrySet()
                .stream()
                .map(stringMultipartFileEntry ->
                        new InterviewContent(Integer.parseInt(stringMultipartFileEntry.getKey()),"image","/place/"+placeInfoEntity.getPartnerId()+"/interview/"+ownerInterviewEntity.getInterviewId()+"/image/"+stringMultipartFileEntry.getKey()))
                .collect(Collectors.toList());

        for (InterviewContent interviewContent: imageContent){
            ownerInterviewEntity.getInterviewContents().add(interviewContent.getContentOrder(),interviewContent);
        }

        storageService.ownerInterviewUploadStepTwo(placeInfoEntity.getPartnerId(),image,ownerInterviewEntity.getInterviewId()); // 파일저장

        return responseService.getSingleResult(interviewService.saveOwnerInterviewEntity(ownerInterviewEntity));

        //return responseService.getSingleResult(interviewService.postOwnerInterviewService());

    }


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
                System.out.println("이게실행대야댐");
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
    @PostMapping(value = "/qna")
    public CommonResult adminAnswer(@RequestBody QnaAnswerEntity qnaAnswerEntity) {

        qnaAnswerEntity.setCreateDt(new Date());
        qnaAnswerEntity.setAnswerId(com.datastax.oss.driver.api.core.uuid.Uuids.timeBased());

        QnaByUserEntity qnaUser = qnaService.findQuestion(qnaAnswerEntity.getQuestionId());

        qnaUser.setStatus("답변완료");
        qnaUser.setRemark(qnaAnswerEntity.getRemark());
        qnaAnswerEntity.setUserId(qnaUser.getUserId());
        qnaService.createAnswer(qnaAnswerEntity);
        qnaService.modifyQuestion(qnaUser);

        return responseService.getSuccessResult();

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
