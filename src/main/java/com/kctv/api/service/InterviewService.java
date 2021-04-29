package com.kctv.api.service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.kctv.api.advice.exception.CRequiredValueException;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.model.interview.InterviewByPlaceIdEntity;
import com.kctv.api.model.interview.OwnerInterviewDto;
import com.kctv.api.model.interview.OwnerInterviewEntity;
import com.kctv.api.repository.ap.PartnerRepository;
import com.kctv.api.repository.interview.InterviewByPlaceIdRepository;
import com.kctv.api.repository.interview.OwnerInterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class InterviewService {

    private final OwnerInterviewRepository ownerInterviewRepository;
    private final InterviewByPlaceIdRepository interviewByPlaceIdRepository;
    private final PartnerRepository partnerRepository;
    public List<OwnerInterviewEntity> findByOwnerInterviewListService(){




        return ownerInterviewRepository.findAll().stream().filter(OwnerInterviewEntity::getStatus).sorted(Comparator.comparingLong(value -> value.getCreateDt().toInstant().getEpochSecond())).limit(5).collect(Collectors.toList());
    }


    public List<OwnerInterviewDto> findByOwnerInterviewListServiceAdminVer(){



        return ownerInterviewRepository.findAll().stream().sorted(Comparator.comparingLong(value -> value.getCreateDt().toInstant().getEpochSecond())).map(ownerInterviewEntity -> new OwnerInterviewDto(ownerInterviewEntity,partnerRepository.findByPartnerId(ownerInterviewEntity.getPlaceId()).orElseThrow(CResourceNotExistException::new))).collect(Collectors.toList());
    }
    public Optional<OwnerInterviewEntity> findByOwnerInterViewService(UUID placeId){


        return ownerInterviewRepository.findByPlaceId(placeId);
    }

    public OwnerInterviewEntity postOwnerInterviewService(OwnerInterviewEntity ownerInterviewEntity){
        Preconditions.checkArgument(!Strings.isNullOrEmpty(ownerInterviewEntity.getTitle()), new CRequiredValueException("제목을 입력해주세요."));

        interviewByPlaceIdRepository.insert(new InterviewByPlaceIdEntity(ownerInterviewEntity.getPlaceId(),ownerInterviewEntity.getInterviewId(),ownerInterviewEntity.getCreateDt()));
        return ownerInterviewRepository.insert(ownerInterviewEntity);
    }

    public OwnerInterviewEntity saveOwnerInterviewEntity(OwnerInterviewEntity ownerInterviewEntity){


        return ownerInterviewRepository.save(ownerInterviewEntity);
    }


    //앱에서 사장님이야기 호출 ()
    public Optional<OwnerInterviewEntity> findByOwnerInterviewEntityByPlaceId(UUID placeId){

        List<InterviewByPlaceIdEntity> interviewByPlaceIdEntityList = interviewByPlaceIdRepository.findByPlaceId(placeId);
        if (interviewByPlaceIdEntityList.size() != 0){
        List<OwnerInterviewEntity> ownerInterviewEntity = ownerInterviewRepository.findByInterviewIdIn(interviewByPlaceIdEntityList.stream().map(InterviewByPlaceIdEntity::getInterviewId).collect(Collectors.toList()));
            return ownerInterviewEntity.size() == 0 ? Optional.empty() : ownerInterviewEntity.stream().filter(OwnerInterviewEntity::getStatus).sorted(Comparator.comparingLong(value -> value.getCreateDt().toInstant().getEpochSecond())).limit(1).findFirst();
        }else {
            return Optional.empty();
        }



    }

    public OwnerInterviewEntity findByInterviewId(UUID interviewId){


        return ownerInterviewRepository.findByInterviewId(interviewId).orElseThrow(CResourceNotExistException::new);
    }

}
