package com.kctv.api.service;

import com.google.common.collect.Lists;
import com.kctv.api.advice.exception.CNotOwnerException;
import com.kctv.api.controller.v1.admin.captive.CaptiveRequest;
import com.kctv.api.entity.admin.ad.CaptivePortalAdEntity;
import com.kctv.api.entity.place.PlaceInfo;
import com.kctv.api.entity.stylecard.StyleCardInfo;
import com.kctv.api.entity.stylecard.CardImageInfo;
import com.kctv.api.repository.ap.PartnerRepository;
import com.kctv.api.repository.card.StyleCardRepository;
import com.kctv.api.repository.file.CardImageInfoRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class StorageService {

    private final String REQUEST_URL = "/image/";
    private final String basePath;
    private final String cardImagePath = "images/cover/card";
    private final String placeImagePath = "images/cover/place";
    private final String AdImagePath = "images/ad";

    private final CardImageInfoRepository cardImageInfoRepository;
    private final StyleCardRepository styleCardRepository;
    private final PartnerRepository partnerRepository;

    public StorageService(@Value("${costom.path.images}") String basePath, CardImageInfoRepository cardImageInfoRepository, StyleCardRepository styleCardRepository, PartnerRepository partnerRepository) {
        this.basePath = basePath;
        this.cardImageInfoRepository = cardImageInfoRepository;
        this.styleCardRepository = styleCardRepository;
        this.partnerRepository = partnerRepository;
    }

    public byte[] getImage(UUID uuid) throws IOException {

        CardImageInfo image = cardImageInfoRepository.findByImageId(uuid).orElseThrow(CNotOwnerException::new);

        Path filePath = Paths.get(basePath+image.getPath()+"/"+image.getFileName()).normalize();

        InputStream imageStream = new FileInputStream(filePath.toString());

        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();

        return imageByteArray;
    }

    public boolean saveImage(UUID uuid, MultipartFile file,String type) throws IOException {


        UUID imageId = UUID.randomUUID();//이미지 아이디 생성
        saveFileIO(file,type,imageId); //실제 파일 저장



        CardImageInfo newImage = CardImageInfo.builder()
                .imageId(imageId)
                .cardId(uuid)
                .createAt(new Date())
                .fileName(StringUtils.cleanPath(imageId.toString()+"_"+file.getOriginalFilename()))
                .build();

        if ("card".equals(type)){
            newImage.setPath(cardImagePath);
            StyleCardInfo styleCardInfo = styleCardRepository.findByCardId(uuid).orElseThrow(IOException::new);
            styleCardInfo.setCoverImage(REQUEST_URL+imageId);
            Optional.ofNullable(styleCardRepository.save(styleCardInfo)).orElseThrow(IOException::new);

        }else if("place".equals(type)){
            newImage.setPath(placeImagePath);
            PlaceInfo placeInfo = partnerRepository.findByPartnerId(uuid).orElseThrow(IOException::new);
            placeInfo.setImages(Lists.newArrayList(REQUEST_URL+imageId));
            Optional.ofNullable(partnerRepository.save(placeInfo)).orElseThrow(IOException::new);
        }

        Optional.ofNullable(cardImageInfoRepository.save(newImage)).orElseThrow(IOException::new);

        return true;

    }



    public boolean saveImage(UUID uuid, List<MultipartFile> file, String type) throws IOException {


        List<String> filePathList = new ArrayList<>();
        for (int i = 0; i < file.size(); i++) {

        UUID imageId = UUID.randomUUID();//이미지 아이디 생성
        saveFileIO(file.get(i),type,imageId); //실제 파일 저장


            CardImageInfo newImage = CardImageInfo.builder()
                    .imageId(imageId)
                    .cardId(uuid)
                    .createAt(new Date())
                    .fileName(StringUtils.cleanPath(imageId.toString()+"_"+file.get(i).getOriginalFilename()))
                    .build();

            newImage.setPath(placeImagePath);
            Optional.ofNullable(cardImageInfoRepository.save(newImage)).orElseThrow(IOException::new);
            filePathList.add(REQUEST_URL+imageId);
        }
            PlaceInfo placeInfo = partnerRepository.findByPartnerId(uuid).orElseThrow(IOException::new);
            placeInfo.setImages(filePathList);
            Optional.ofNullable(partnerRepository.save(placeInfo)).orElseThrow(IOException::new);

        return true;

    }

    public CaptivePortalAdEntity saveAdImgOutput(MultipartFile multipartFile, UUID adId) throws IOException {
        Path directory = Paths.get(basePath + AdImagePath).normalize();
        Files.createDirectories(directory);





/*


        String fileName = StringUtils.cleanPath(adId.toString()+"_"+file.getOriginalFilename());  // 파일명을 바르게 수정한다.
*/

        return null;


    }

    public void saveFileIO(MultipartFile file,String type,UUID imageId) throws IOException{

        Path directory = null;

        if ("card".equals(type)) {
            directory = Paths.get(basePath + cardImagePath).normalize();  // parent directory를 찾는다. C~~ data/
        } else{
            directory = Paths.get(basePath + placeImagePath).normalize();  // parent directory를 찾는다. C~~ data/
        }

        Files.createDirectories(directory);  // directory 해당 경로까지 디렉토리를 모두 만든다.

        String fileName = StringUtils.cleanPath(imageId.toString()+"_"+file.getOriginalFilename());  // 파일명을 바르게 수정한다.

        Assert.state(!fileName.contains(".."), "Name of file cannot contain '..'");       // 파일명에 '..' 문자가 들어 있다면 오류를 발생하고 아니라면 진행(해킹및 오류방지)

        Path targetPath = directory.resolve(fileName).normalize(); // 파일을 저장할 경로를 Path 객체로 받는다.



        Assert.state(!Files.exists(targetPath), fileName + " File alerdy exists.");  // 파일이 이미 존재하는지 확인하여 존재한다면 오류를 발생하고 없다면 저장한다.

        file.transferTo(targetPath);

    }



}
