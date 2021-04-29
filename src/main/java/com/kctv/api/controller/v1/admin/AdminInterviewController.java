package com.kctv.api.controller.v1.admin;


import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.kctv.api.advice.exception.CNotOwnerException;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.model.interview.InterviewContent;
import com.kctv.api.model.interview.OwnerInterviewDto;
import com.kctv.api.model.interview.OwnerInterviewEntity;
import com.kctv.api.model.place.PlaceInfoEntity;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Api(tags = {"16. AdminInterview API"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/admin/place")
public class AdminInterviewController {


    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ResponseService responseService;
    private final PlaceService placeService;
    private final StorageService storageService;
    private final InterviewService interviewService;



    @ApiOperation(value = "관리자용 사장님 이야기 상세보기", notes = "사장님이야기 목록을 호출한다.")
    @GetMapping("/interview/{interviewId}")
    public SingleResult<OwnerInterviewDto> getOwnerInterviewDetail(@PathVariable("interviewId") UUID interviewId){

        OwnerInterviewEntity ownerInterviewEntity = interviewService.findByInterviewId(interviewId);
        PlaceInfoEntity placeInfoEntity = placeService.getPartnerByIdService(ownerInterviewEntity.getPlaceId()).orElseThrow(CNotOwnerException::new);
        OwnerInterviewDto dto =new OwnerInterviewDto(ownerInterviewEntity,placeInfoEntity);
        return responseService.getSingleResult(dto.dateFormatter("yy.MM.dd HH:mm:ss"));
    }



    @ApiOperation(value = "사장님 이야기 전체목록", notes = "사장님이야기 목록을 호출한다.")
    @GetMapping("/interview")
    public ListResult<OwnerInterviewDto> getOwnerInterviewList(){
        return responseService.getListResult(interviewService.findByOwnerInterviewListServiceAdminVer());
    }


    @ApiOperation(value = "사장님이야기 상태변경", notes = "사장님이야기의 상태를 변경한다.")
    @PutMapping(value = "/interview/{interviewId}/status")
    public SingleResult<OwnerInterviewEntity> modifyStatus(@PathVariable("interviewId") UUID interviewId){
        System.out.println("머야");

        OwnerInterviewEntity ownerInterviewEntity = interviewService.findByInterviewId(interviewId);

        ownerInterviewEntity.setStatus(ownerInterviewEntity.getStatus() ? false : true);

        return responseService.getSingleResult(interviewService.saveOwnerInterviewEntity(ownerInterviewEntity));

    }






    @ApiOperation(value = "사장님이야기 등록 step.1", notes = "장소 ID를 통해 사장님이야기 등록",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "title", value = "요청 본문", dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "ownerSaying", value = "요청 본문", dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "cover", value = "커버 이미지", dataType = "file", paramType = "form")
    })
    @PostMapping(value = "/{placeId}/interview/1",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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
        ownerInterviewEntity.setInterviewId(Uuids.timeBased());
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
    @PutMapping(value = "/interview/{interviewId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SingleResult<OwnerInterviewEntity> modifyOwnerInterviewStep1(@PathVariable("interviewId")UUID interviewId,
                                                                        @RequestParam(required = false) String title,
                                                                        @RequestParam(required = false) String ownerSaying,
                                                                        @ApiIgnore @RequestPart(required = false) MultipartFile cover
    ) throws IOException {

        //OwnerInterviewEntity ownerInterviewEntity = new ObjectMapper().readValue(request,OwnerInterviewEntity.class);

        OwnerInterviewEntity beforeEntity = interviewService.findByInterviewId(interviewId);

        if (!Strings.isNullOrEmpty(title))
            beforeEntity.setTitle(title);
        if (!Strings.isNullOrEmpty(ownerSaying))
            beforeEntity.setOwnerSaying(ownerSaying);

        beforeEntity.setModifyDt(new Date());

        //인터뷰컨텐츠 객체중 키값이 'cover'라면 커버이미지로 판단

        if (cover != null){

            storageService.ownerInterviewModifyCover(beforeEntity.getPlaceId(),cover,beforeEntity.getInterviewId());
        }
        OwnerInterviewEntity result = interviewService.saveOwnerInterviewEntity(beforeEntity);

        return responseService.getSingleResult(result);

        //return responseService.getSingleResult(interviewService.postOwnerInterviewService());

    }



    @ApiOperation(value = "사장님이야기 수정 step.2", notes = "장소 ID를 통해 사장님이야기 수정",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "3", value = "파일테스트", dataType = "file", paramType = "form"),
            @ApiImplicitParam(name = "request", value = "파일테스트", dataType = "string", paramType = "form",defaultValue = "[   {     \"content\": \"컨텐츠제목\",     \"contentOrder\": 0,     \"contentType\": \"sub\"   },   {     \"content\": \"컨텐츠내용\",     \"contentOrder\": 1,     \"contentType\": \"text\"   },{     \"content\": \"컨텐츠내용둘\",     \"contentOrder\": 2,     \"contentType\": \"text\"   } ]")

    })
    @PutMapping(value = "/{placeId}/interview/{interviewId}/2",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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
    @PostMapping(value = "/{placeId}/interview/{interviewId}/2",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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

}
