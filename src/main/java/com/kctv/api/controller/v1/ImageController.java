package com.kctv.api.controller.v1;

import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.StorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//file:///C:/images
@Api(tags = {"05. Images API"})
@RequiredArgsConstructor
@RestController
public class ImageController {

    private final ResponseService responseService;
    private final StorageService storageService;


    @ApiOperation(value = "이미지 출력 API", notes = "이미지 UUID로 이미지를 출력한다.")
    @GetMapping(value = "/image/{imageid}",produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getCoverImage(
            @ApiParam(value = "이미지ID", required = true) @PathVariable("imageid")String imageid,
            HttpServletRequest request) throws IOException {

        return storageService.getImage(UUID.fromString(imageid));

    }

    @ApiOperation(value = "커버 사진 저장 API", notes = "라이프스타일카드의 ID를 통해 커버사진을 업로드한다.")
    @RequestMapping(value = "/image/{id}", method = RequestMethod.POST, produces = "application/json;charset=utf-8", consumes = {"multipart/form-data"})
    public SingleResult<Boolean> uploadImage(@ApiParam(value = "라이프스타일카드 ID")@PathVariable("id") UUID id, @RequestParam("file") MultipartFile file) throws IOException {

        System.out.println(file.getOriginalFilename());
        if (file.getOriginalFilename() != null && !file.getOriginalFilename().endsWith(".png")
                && !file.getOriginalFilename().endsWith(".jpg")
                && !file.getOriginalFilename().endsWith(".gif")) {
            throw new CResourceNotExistException("Only PNG/GIF/JPG file accepted.");
        }



        return responseService.getSingleResult(storageService.saveImage(id,file));
    }
}

