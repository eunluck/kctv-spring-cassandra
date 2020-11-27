package com.kctv.api.service;

import com.kctv.api.advice.exception.CNotOwnerException;
import com.kctv.api.entity.tag.StyleCardInfo;
import com.kctv.api.model.CardImageInfo;
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
import java.util.Date;
import java.util.Optional;
import java.util.UUID;



@Service
public class StorageService {

    private final String REQUEST_URL = "/image/";
    private final String basePath;
    private final String parentPath = "images/cover/";
    private final CardImageInfoRepository cardImageInfoRepository;
    private final StyleCardRepository styleCardRepository;

    public StorageService(@Value("${costom.path.images}") String basePath, CardImageInfoRepository cardImageInfoRepository, StyleCardRepository styleCardRepository) {
        this.basePath = basePath;
        this.cardImageInfoRepository = cardImageInfoRepository;
        this.styleCardRepository = styleCardRepository;
    }

    public byte[] getImage(UUID uuid) throws IOException {

        CardImageInfo image = cardImageInfoRepository.findByImageId(uuid).orElseThrow(CNotOwnerException::new);

        Path filePath = Paths.get(basePath+image.getPath()+"/"+image.getFileName()).normalize();

        InputStream imageStream = new FileInputStream(filePath.toString());

        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();

        return imageByteArray;
    }

    public boolean saveImage(UUID uuid, MultipartFile file) throws IOException {


        saveFileIO(file); //실제 파일 저장

        UUID imageId = UUID.randomUUID();//이미지 아이디 생성

        CardImageInfo newImage = CardImageInfo.builder()
                .imageId(imageId)
                .cardId(uuid)
                .createAt(new Date())
                .fileName(StringUtils.cleanPath(file.getOriginalFilename()))
                .path("images/cover") //TODO 구체화필요
                .build();

        Optional.ofNullable(cardImageInfoRepository.save(newImage)).orElseThrow(IOException::new);

        StyleCardInfo styleCardInfo = styleCardRepository.findByCardId(uuid).orElseThrow(IOException::new);
        styleCardInfo.setCoverImage(REQUEST_URL+imageId);

        Optional.ofNullable(styleCardRepository.save(styleCardInfo)).orElseThrow(IOException::new);

        return true;

    }

    public void saveFileIO(MultipartFile file) throws IOException{

        System.out.println("0::"+basePath+parentPath);
        Path directory = Paths.get(basePath+parentPath).normalize();  // parent directory를 찾는다. C~~ data/

        System.out.println("1::"+directory);
        Files.createDirectories(directory);  // directory 해당 경로까지 디렉토리를 모두 만든다.

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());  // 파일명을 바르게 수정한다.

        System.out.println("2::"+fileName);

        Assert.state(!fileName.contains(".."), "Name of file cannot contain '..'");       // 파일명에 '..' 문자가 들어 있다면 오류를 발생하고 아니라면 진행(해킹및 오류방지)

        Path targetPath = directory.resolve(fileName).normalize(); // 파일을 저장할 경로를 Path 객체로 받는다.

        System.out.println("3::"+targetPath);

        Assert.state(!Files.exists(targetPath), fileName + " File alerdy exists.");  // 파일이 이미 존재하는지 확인하여 존재한다면 오류를 발생하고 없다면 저장한다.
        file.transferTo(targetPath);

    }


}
