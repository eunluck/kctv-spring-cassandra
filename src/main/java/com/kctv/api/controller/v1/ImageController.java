package com.kctv.api.controller.v1;

import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.StorageService;
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
@RequiredArgsConstructor
@RestController
public class ImageController {

    private final ResponseService responseService;
    private final StorageService storageService;

    @GetMapping(value = "/image/test",produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImages(
            /*@PathVariable("filepath")String filepath,
            @PathVariable(value = "extension",required = false, name = "png")String extension,
            HttpServletRequest request*/) throws IOException {

        InputStream imageStream = new FileInputStream("/images/qwie92j.jpg");
        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();

        return imageByteArray;
    }



    //uuid로 호출한다
    //uuid로 디비에 검색한다
    //TODO



    //192.168.0.56:8081/image/test/filepath

    @GetMapping(value = "/image/{imageid}",produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getCoverImage(
            @PathVariable("imageid")String imageid,
            HttpServletRequest request) throws IOException {

        return storageService.getImage(UUID.fromString(imageid));

    }

    @RequestMapping(value = "/image/{id}", method = RequestMethod.POST, produces = "application/json;charset=utf-8", consumes = {"multipart/form-data"})
    public SingleResult<Boolean> uploadImage(@PathVariable("id") UUID id, @RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("왜이래");

        System.out.println(file.getOriginalFilename());
        if (file.getOriginalFilename() != null && !file.getOriginalFilename().endsWith(".png")
                && !file.getOriginalFilename().endsWith(".jpg")
                && !file.getOriginalFilename().endsWith(".gif")) {
            throw new CResourceNotExistException("Only PNG/GIF/JPG file accepted.");
        }



        return responseService.getSingleResult(storageService.saveImage(id,file));
    }
}

