package com.kctv.api.controller.v1;

import com.kctv.api.advice.exception.CResourceNotExistException;

import com.kctv.api.controller.v1.admin.captive.CaptiveAdEntity;
import com.kctv.api.entity.admin.ad.CaptivePortalAdEntity;
import com.kctv.api.model.response.ListResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.CaptivePortalAdService;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.StorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;

//file:///C:/images
@Api(tags = {"06. Images API"})
@RequiredArgsConstructor
@RestController
public class ImageController {

    private final ResponseService responseService;
    private final StorageService storageService;
    private final CaptivePortalAdService captivePortalAdService;


    @ApiOperation(value = "이미지 출력 API", notes = "이미지 UUID로 이미지를 출력한다.")
    @GetMapping(value = "/image/{imageid}",produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getCoverImage(
            @ApiParam(value = "이미지ID", required = true) @PathVariable("imageid")String imageid) throws IOException {

        return storageService.getImage(UUID.fromString(imageid));

    }


    @ApiOperation(value = "캡티브포탈 AD 이미지 출력API", notes = "이미지 UUID로 이미지를 출력한다.")
    @GetMapping(value = "/image/ad/{imageid}",produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getAdImage(
            @ApiParam(value = "이미지ID", required = true) @PathVariable("imageid")String imageid) throws IOException {

        return storageService.getAdImage(UUID.fromString(imageid));

    }


    @ApiOperation(value = "캡티브포탈 리스트 출력API", notes = "캡티브 포탈 AD에 등록된 이미지 중 ACTIVE상태인 목록을 조회한다.")
    @GetMapping(value = "/ad")
    public ListResult<CaptivePortalAdEntity> getAdList() throws IOException {

        return responseService.getListResult(captivePortalAdService.findAllAd().stream().filter(adEntity -> adEntity.getAdStatus().equals("Active")).limit(4).collect(Collectors.toList()));

    }
/*
    @ApiOperation(value = "스타일카드 커버 사진 저장 API", notes = "라이프스타일카드의 ID를 통해 커버사진을 업로드한다.")
    @RequestMapping(value = "/image/{sourceId}/card", method = RequestMethod.POST, produces = "application/json;charset=utf-8", consumes = {"multipart/form-data"})
    public SingleResult<String> uploadImageCard(@ApiParam(value = "라이프스타일카드 ID")@PathVariable("sourceId") UUID id, @RequestParam("file") MultipartFile file) throws IOException {

        if (file.getOriginalFilename() != null && !file.getOriginalFilename().endsWith(".png")
                && !file.getOriginalFilename().endsWith(".jpg")
                && !file.getOriginalFilename().endsWith(".gif")) {
            throw new CResourceNotExistException("Only PNG/GIF/JPG file accepted.");
        }



        return responseService.getSingleResult(storageService.saveImage(id,file,"card"));
    }


    @ApiOperation(value = "가게 커버 사진 저장 API", notes = "가게의 ID를 통해 커버사진을 업로드한다.")
    @RequestMapping(value = "/image/{sourceId}/place", method = RequestMethod.POST, produces = "application/json;charset=utf-8", consumes = {"multipart/form-data"})
    public SingleResult<String> uploadImagePlace(@ApiParam(value = "라이프스타일카드 ID")@PathVariable("sourceId") UUID id, @RequestParam("file") MultipartFile file) throws IOException {

        System.out.println(file.getOriginalFilename());
        if (file.getOriginalFilename() != null && !file.getOriginalFilename().endsWith(".png")
                && !file.getOriginalFilename().endsWith(".jpg")
                && !file.getOriginalFilename().endsWith(".gif")) {
            throw new CResourceNotExistException("Only PNG/GIF/JPG file accepted.");
        }



        return responseService.getSingleResult(storageService.saveImage(id,file,"place"));
    }*/
}

