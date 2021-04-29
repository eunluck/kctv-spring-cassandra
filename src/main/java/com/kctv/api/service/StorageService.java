package com.kctv.api.service;

import com.google.common.collect.Lists;
import com.kctv.api.advice.exception.CFormatNotAllowedException;
import com.kctv.api.advice.exception.CNotOwnerException;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.controller.v1.admin.captive.CaptiveRequest;
import com.kctv.api.model.admin.ad.CaptivePortalAdEntity;
import com.kctv.api.model.place.PlaceInfoEntity;
import com.kctv.api.model.stylecard.StyleCardInfoEntity;
import com.kctv.api.model.stylecard.CardImageInfoEntity;
import com.kctv.api.repository.ad.CaptivePortalAdRepository;
import com.kctv.api.repository.ap.PartnerRepository;
import com.kctv.api.repository.card.StyleCardRepository;
import com.kctv.api.repository.file.CardImageInfoRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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
    private final String interviewImagePath = "images/interview";

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


    public List<String> getFileFullName(String param,File dir){

        String[] fileName = dir.list();
        List<String> paramFile = Lists.newArrayList();

        for (int i = 0; i < fileName.length; i++) {

            if (fileName[i].startsWith(param)){
                paramFile.add(fileName[i]);

            }
        }

        return paramFile;
    }

    public byte[] getInterviewImage(String placeId,String interviewId,String param) throws IOException {

        File dir = new File(basePath+interviewImagePath+"/"+placeId+"/"+interviewId);
        String[] fileName = dir.list();


        String paramFile = null;

        for (int i = 0; i < fileName.length; i++) {

            if (fileName[i].startsWith(param)){
                paramFile = fileName[i];
            }
        }
        Path filePath = Paths.get(basePath+interviewImagePath+"/"+placeId+"/"+interviewId+"/"+paramFile);

        InputStream imageStream = new FileInputStream(filePath.toString());

        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();

        return imageByteArray;
    }



    public byte[] getImage(UUID uuid) throws IOException {

        CardImageInfoEntity image = cardImageInfoRepository.findByImageId(uuid).orElseThrow(CNotOwnerException::new);

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

    public String appendPlaceImage(PlaceInfoEntity placeInfoEntity, MultipartFile file) throws IOException {
        UUID imgId = UUID.randomUUID();

        String fileName = saveFileIO(file,"place",imgId);

        CardImageInfoEntity newImage = CardImageInfoEntity.builder()
                .imageId(imgId)
                .cardId(placeInfoEntity.getPartnerId())
                .createAt(new Date())
                .path(placeImagePath)
                .fileName(fileName)
                .build();

        CardImageInfoEntity cardImageInfoEntity = cardImageInfoRepository.save(newImage);

        return REQUEST_URL+imgId;
    }

    public boolean removePlaceImage(UUID imgId){


        CardImageInfoEntity cardImageInfoEntity = cardImageInfoRepository.findByImageId(imgId).orElseThrow(CResourceNotExistException::new);
        try {

        Path directory = Paths.get(basePath+ cardImageInfoEntity.getPath()+"/"+ cardImageInfoEntity.getFileName()).normalize();

        Files.deleteIfExists(directory);
        }catch (IOException e){
            e.printStackTrace();
            cardImageInfoRepository.save(cardImageInfoEntity);
            return false;
        }

        return true;
    }


    public String saveImage(UUID uuid, MultipartFile file,String type) throws IOException {

        UUID imageId = UUID.randomUUID();//이미지 아이디 생성
        String fileName = saveFileIO(file,type,imageId); //실제 파일 저장



        CardImageInfoEntity newImage = CardImageInfoEntity.builder()
                .imageId(imageId)
                .cardId(uuid)
                .createAt(new Date())
                .fileName(fileName)
                .build();

        if ("card".equals(type)){
            newImage.setPath(cardImagePath);
            StyleCardInfoEntity styleCardInfoEntity = styleCardRepository.findByCardId(uuid).orElseThrow(IOException::new);
            styleCardInfoEntity.setCoverImage(REQUEST_URL+imageId);
            styleCardRepository.save(styleCardInfoEntity);

        }else if("place".equals(type)){
            newImage.setPath(placeImagePath);
            PlaceInfoEntity placeInfoEntity = partnerRepository.findByPartnerId(uuid).orElseThrow(IOException::new);
            placeInfoEntity.setImages(Lists.newArrayList(REQUEST_URL+imageId));
            partnerRepository.save(placeInfoEntity);
        }

        CardImageInfoEntity afterImg = Optional.of(cardImageInfoRepository.save(newImage)).orElseThrow(IOException::new);

        return afterImg.getPath();

    }



    public List<String> saveImage(UUID uuid, List<MultipartFile> file, String type) throws IOException {


        List<String> filePathList = new ArrayList<>();
        for (MultipartFile multipartFile : file) {
            checkImgType(multipartFile);

            UUID imageId = UUID.randomUUID();//이미지 아이디 생성
            String fileName = saveFileIO(multipartFile, type, imageId); //실제 파일 저장


            CardImageInfoEntity newImage = CardImageInfoEntity.builder()
                    .imageId(imageId)
                    .cardId(uuid)
                    .createAt(new Date())
                    .fileName(fileName)
                    .build();

            newImage.setPath(placeImagePath);
            cardImageInfoRepository.save(newImage);
            filePathList.add(REQUEST_URL + imageId);
        }
            PlaceInfoEntity placeInfoEntity = partnerRepository.findByPartnerId(uuid).orElseThrow(IOException::new);
            placeInfoEntity.setImages(filePathList);
            partnerRepository.save(placeInfoEntity);

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
                .imgPath(adImagePath+"/"+randomAdId.toString()+"_"+Normalizer.normalize(Objects.requireNonNull(request.getImgFile().getOriginalFilename()), Normalizer.Form.NFC))
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
        CardImageInfoEntity cardImageInfoEntity = cardImageInfoRepository.findByImageId(imgId).orElseThrow(CResourceNotExistException::new);

        Path directory = Paths.get(basePath+ cardImageInfoEntity.getPath()+"/"+ cardImageInfoEntity.getFileName()).normalize();

        if (Files.deleteIfExists(directory)){
            cardImageInfoRepository.delete(cardImageInfoEntity);
            return true;
        }else {
            return false;
        }


    }

    public boolean deleteFile(List<UUID> imgIds) throws IOException {
        for (UUID imgId : imgIds) {

            CardImageInfoEntity cardImageInfoEntity = cardImageInfoRepository.findByImageId(imgId).orElseThrow(CResourceNotExistException::new);
            Path directory = Paths.get(basePath+ cardImageInfoEntity.getPath()+"/"+ cardImageInfoEntity.getFileName()).normalize();
            if (Files.deleteIfExists(directory)) {
                cardImageInfoRepository.delete(cardImageInfoEntity);
            }else {
                return false;
            }
        }
        return true;
    }


/*

    public void ownerInterviewUpload(UUID placeId, Map<String,MultipartFile> files,UUID interviewId) throws IOException {

        // cover : file
        // 1 : file
        Path directory = Paths.get(basePath + interviewImagePath +"/" +placeId+"/"+interviewId).normalize();
        Path path = Files.createDirectories(directory);


        files.forEach((s, file) -> {
            try {
                saveOwnerInterviewFile(path.resolve(s+file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."))).normalize(),s,file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        System.out.println(path.toString());

    }
*/


    public void ownerInterviewModifyCover(UUID placeId, MultipartFile cover,UUID interviewId) throws IOException {

/*

        String[] fileName = dir.list();


        String paramFile = null;

        if (fileName ==null || fileName.length == 0){
            return;
        }
        for (int i = 0; i < fileName.length; i++) {

            if (fileName[i].startsWith("cover")){
                paramFile = fileName[i];
            }
        }
*/
        Path directory = Paths.get(basePath + interviewImagePath +"/" +placeId+"/"+interviewId).normalize();
        saveOwnerInterviewFile(directory.resolve("cover"+cover.getOriginalFilename().substring(cover.getOriginalFilename().lastIndexOf("."))).normalize(),"cover",cover);

    }

    public void ownerInterviewUploadStepOne(UUID placeId, MultipartFile cover,UUID interviewId) throws IOException {

        Path directory = Paths.get(basePath + interviewImagePath +"/" +placeId+"/"+interviewId).normalize();
        Path path = Files.createDirectories(directory);

        saveOwnerInterviewFile(path.resolve("cover"+cover.getOriginalFilename().substring(cover.getOriginalFilename().lastIndexOf("."))).normalize(),"cover",cover);

    }



    public void ownerInterviewUploadStepTwo(UUID placeId, Map<String,MultipartFile> files,UUID interviewId) throws IOException {

        // cover : file
        // 1 : file
        Path directory = Paths.get(basePath + interviewImagePath +"/" +placeId+"/"+interviewId).normalize();
        Path path = Files.createDirectories(directory);


        files.forEach((s, file) -> {
            try {

                saveOwnerInterviewFile(path.resolve(s+file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."))).normalize(),s,file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }



    public void saveOwnerInterviewFile(Path path, String key, MultipartFile file) throws IOException {


        Assert.state(!file.getOriginalFilename().contains(".."), "Name of file cannot contain '..'");       // 파일명에 '..' 문자가 들어 있다면 오류를 발생하고 아니라면 진행(해킹및 오류방지)
        List<String> fileList = getFileFullName(key,path.toFile().getParentFile());


/*

        String fileName = file.getOriginalFilename();
        fileName.substring(fileName.lastIndexOf("/"));
*/
        if (!CollectionUtils.isEmpty(fileList)) {
            for (String s : fileList) {
                Files.deleteIfExists(path.getParent().resolve(s));
            }

        }

        file.transferTo(path);
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

        String fileName = StringUtils.cleanPath(imageId.toString()+"_"+Normalizer.normalize(Objects.requireNonNull(file.getOriginalFilename()), Normalizer.Form.NFC));  // 파일명을 바르게 수정한다.

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
