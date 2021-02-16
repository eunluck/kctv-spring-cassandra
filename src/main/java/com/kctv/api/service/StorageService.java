package com.kctv.api.service;

import com.google.common.collect.Lists;
import com.kctv.api.advice.exception.CFormatNotAllowedException;
import com.kctv.api.advice.exception.CNotOwnerException;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.controller.v1.admin.captive.CaptiveRequest;
import com.kctv.api.entity.admin.ad.CaptivePortalAdEntity;
import com.kctv.api.entity.place.PlaceInfo;
import com.kctv.api.entity.stylecard.StyleCardInfo;
import com.kctv.api.entity.stylecard.CardImageInfo;
import com.kctv.api.repository.ad.CaptivePortalAdRepository;
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
import java.text.Normalizer;
import java.util.*;


@Service
public class StorageService {

    private final String REQUEST_URL = "/image/";
    private final String AD_REQUEST_URL = "/image/ad/";
    private final String basePath;
    private final String cardImagePath = "images/cover/card";
    private final String placeImagePath = "images/cover/place";
    private final String adImagePath = "images/ad";

    private final CardImageInfoRepository cardImageInfoRepository;
    private final StyleCardRepository styleCardRepository;
    private final PartnerRepository partnerRepository;
    private final CaptivePortalAdRepository captivePortalAdRepository;

    public StorageService(@Value("${costom.path.images}") String basePath, CardImageInfoRepository cardImageInfoRepository, StyleCardRepository styleCardRepository, PartnerRepository partnerRepository, CaptivePortalAdRepository captivePortalAdRepository) {
        this.basePath = basePath;
        this.cardImageInfoRepository = cardImageInfoRepository;
        this.styleCardRepository = styleCardRepository;
        this.partnerRepository = partnerRepository;
        this.captivePortalAdRepository = captivePortalAdRepository;
    }

    public byte[] getImage(UUID uuid) throws IOException {

        CardImageInfo image = cardImageInfoRepository.findByImageId(uuid).orElseThrow(CNotOwnerException::new);

        Path filePath = Paths.get(basePath+image.getPath()+"/"+image.getFileName()).normalize();

        InputStream imageStream = new FileInputStream(filePath.toString());

        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();

        return imageByteArray;
    }

    public byte[] getAdImage(UUID uuid) throws IOException {

        CaptivePortalAdEntity image = Optional.ofNullable(captivePortalAdRepository.findByAdId(uuid)).orElseThrow(CNotOwnerException::new);


        Path filePath = Paths.get(basePath+image.getImgPath()).normalize();

        InputStream imageStream = new FileInputStream(filePath.toString());

        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();

        return imageByteArray;
    }

    public String appendPlaceImage(PlaceInfo placeInfo,MultipartFile file) throws IOException {
        UUID imgId = UUID.randomUUID();

        String fileName = saveFileIO(file,"place",imgId);

        CardImageInfo newImage = CardImageInfo.builder()
                .imageId(imgId)
                .cardId(placeInfo.getPartnerId())
                .createAt(new Date())
                .path(placeImagePath)
                .fileName(fileName)
                .build();

        CardImageInfo cardImageInfo = cardImageInfoRepository.save(newImage);

        return REQUEST_URL+imgId;
    }

    public boolean removePlaceImage(UUID imgId){


        CardImageInfo cardImageInfo = cardImageInfoRepository.findByImageId(imgId).orElseThrow(CResourceNotExistException::new);
        try {

        Path directory = Paths.get(basePath+cardImageInfo.getPath()+"/"+cardImageInfo.getFileName()).normalize();

        Files.deleteIfExists(directory);
        }catch (IOException e){
            e.printStackTrace();
            cardImageInfoRepository.save(cardImageInfo);
            return false;
        }

        return true;
    }


    public String saveImage(UUID uuid, MultipartFile file,String type) throws IOException {

        UUID imageId = UUID.randomUUID();//이미지 아이디 생성
        String fileName = saveFileIO(file,type,imageId); //실제 파일 저장



        CardImageInfo newImage = CardImageInfo.builder()
                .imageId(imageId)
                .cardId(uuid)
                .createAt(new Date())
                .fileName(fileName)
                .build();

        if ("card".equals(type)){
            newImage.setPath(cardImagePath);
            StyleCardInfo styleCardInfo = styleCardRepository.findByCardId(uuid).orElseThrow(IOException::new);
            styleCardInfo.setCoverImage(REQUEST_URL+imageId);
            Optional.of(styleCardRepository.save(styleCardInfo)).orElseThrow(IOException::new);

        }else if("place".equals(type)){
            newImage.setPath(placeImagePath);
            PlaceInfo placeInfo = partnerRepository.findByPartnerId(uuid).orElseThrow(IOException::new);
            placeInfo.setImages(Lists.newArrayList(REQUEST_URL+imageId));
            Optional.of(partnerRepository.save(placeInfo)).orElseThrow(IOException::new);
        }

        CardImageInfo afterImg = Optional.of(cardImageInfoRepository.save(newImage)).orElseThrow(IOException::new);

        return afterImg.getPath();

    }



    public List<String> saveImage(UUID uuid, List<MultipartFile> file, String type) throws IOException {


        List<String> filePathList = new ArrayList<>();
        for (MultipartFile multipartFile : file) {
            checkImgType(multipartFile);

            UUID imageId = UUID.randomUUID();//이미지 아이디 생성
            String fileName = saveFileIO(multipartFile, type, imageId); //실제 파일 저장


            CardImageInfo newImage = CardImageInfo.builder()
                    .imageId(imageId)
                    .cardId(uuid)
                    .createAt(new Date())
                    .fileName(fileName)
                    .build();

            newImage.setPath(placeImagePath);
            Optional.of(cardImageInfoRepository.save(newImage)).orElseThrow(IOException::new);
            filePathList.add(REQUEST_URL + imageId);
        }
            PlaceInfo placeInfo = partnerRepository.findByPartnerId(uuid).orElseThrow(IOException::new);
            placeInfo.setImages(filePathList);
            Optional.of(partnerRepository.save(placeInfo)).orElseThrow(IOException::new);

        return filePathList;

    }

    public CaptivePortalAdEntity saveAdImg(CaptiveRequest request) throws IOException {


        //Normalizer.normalize(request.getImgFile().getOriginalFilename(), Normalizer.Form.NFC)
        checkImgType(request.getImgFile());

        UUID randomAdId = UUID.randomUUID();

        String fileName = saveFileIO(request.getImgFile(),"ad",randomAdId);
        CaptivePortalAdEntity saveAd = CaptivePortalAdEntity.builder().adCreateDt(new Date())
                .adId(randomAdId)
                .adStatus(request.getStatus())
                .adStartDt(request.getStartDate())
                .adEndDt(request.getEndDate())
                .adLink(request.getLink())
                .imgName(request.getImgFile().getOriginalFilename())
                .imgPath(adImagePath+"/"+randomAdId.toString()+"_"+Normalizer.normalize(request.getImgFile().getOriginalFilename(), Normalizer.Form.NFC))
                .imgUrl(AD_REQUEST_URL+randomAdId)
                .build();



        return captivePortalAdRepository.save(saveAd);

    }

    public String getImgIdByImgUrl(String imgUrl){

        return imgUrl.replace(REQUEST_URL,"");
    }

    public boolean deleteAdFile(CaptivePortalAdEntity adEntity) throws IOException {
        Path directory = Paths.get(basePath+adEntity.getImgPath()).normalize();
        return Files.deleteIfExists(directory);
    }

    public boolean deleteFile(UUID imgId) throws IOException {
        CardImageInfo cardImageInfo = cardImageInfoRepository.findByImageId(imgId).orElseThrow(CResourceNotExistException::new);

        Path directory = Paths.get(basePath+cardImageInfo.getPath()+"/"+cardImageInfo.getFileName()).normalize();

        if (Files.deleteIfExists(directory)){
            cardImageInfoRepository.delete(cardImageInfo);
            return true;
        }else {
            return false;
        }


    }

    public boolean deleteFile(List<UUID> imgIds) throws IOException {
        for (UUID imgId : imgIds) {

            CardImageInfo cardImageInfo = cardImageInfoRepository.findByImageId(imgId).orElseThrow(CResourceNotExistException::new);
            Path directory = Paths.get(basePath+cardImageInfo.getPath()+"/"+cardImageInfo.getFileName()).normalize();
            if (Files.deleteIfExists(directory)) {
                cardImageInfoRepository.delete(cardImageInfo);
            }else {
                return false;
            }
        }
        return true;
    }


    public String saveFileIO(MultipartFile file,String type,UUID imageId) throws IOException{

        Path directory = null;

        if ("card".equals(type)) {
            directory = Paths.get(basePath + cardImagePath).normalize();  // parent directory를 찾는다. C~~ data/
        } else if("place".equals(type)){
            directory = Paths.get(basePath + placeImagePath).normalize();  // parent directory를 찾는다. C~~ data/
        } else if("ad".equals(type)){
            directory = Paths.get(basePath + adImagePath).normalize();
        }
        Files.createDirectories(directory);  // directory 해당 경로까지 디렉토리를 모두 만든다.

        String fileName = StringUtils.cleanPath(imageId.toString()+"_"+Normalizer.normalize(file.getOriginalFilename(), Normalizer.Form.NFC));  // 파일명을 바르게 수정한다.

        Assert.state(!fileName.contains(".."), "Name of file cannot contain '..'");       // 파일명에 '..' 문자가 들어 있다면 오류를 발생하고 아니라면 진행(해킹및 오류방지)

        Path targetPath = directory.resolve(fileName).normalize(); // 파일을 저장할 경로를 Path 객체로 받는다.



        Assert.state(!Files.exists(targetPath), fileName + " File alerdy exists.");  // 파일이 이미 존재하는지 확인하여 존재한다면 오류를 발생하고 없다면 저장한다.

        file.transferTo(targetPath);

        return fileName;
    }



    public void checkImgType(MultipartFile file){
        if (file.getOriginalFilename() != null && !file.getOriginalFilename().endsWith(".png")
                && !file.getOriginalFilename().endsWith(".jpg")
                && !file.getOriginalFilename().endsWith(".jpeg")
                && !file.getOriginalFilename().endsWith(".gif")) {
            throw new CFormatNotAllowedException("Only PNG/GIF/JPG file accepted.");
        }
    }


    public void checkImgType(List<MultipartFile> files){
        for (MultipartFile file: files){
            if (file.getOriginalFilename() != null && !file.getOriginalFilename().endsWith(".png")
                    && !file.getOriginalFilename().endsWith(".jpg")
                    && !file.getOriginalFilename().endsWith(".jpeg")
                    && !file.getOriginalFilename().endsWith(".gif")) {
                throw new CFormatNotAllowedException("Only PNG/GIF/JPG file accepted.");
            }
        }
    }



}
