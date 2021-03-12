package com.kctv.api.service;

import com.google.common.base.Preconditions;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.model.admin.FaqTableEntity;
import com.kctv.api.repository.faq.FaqRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@AllArgsConstructor
@Data
public class FaqService {


    public final FaqRepository faqRepository;

    public List<FaqTableEntity> findByAll(){

        return faqRepository.findAll();
    }


    public FaqTableEntity findById(UUID uuid){
        return faqRepository.findByFaqId(uuid).orElseThrow(CResourceNotExistException::new);
    }

    public FaqTableEntity faqSaveOrUpdate(FaqTableEntity faqTableEntity){
        return faqRepository.save(faqTableEntity);
    }

    public FaqTableEntity postFaq(String question, String answer){
        Preconditions.checkNotNull(question);
        Preconditions.checkNotNull(answer);

        FaqTableEntity faq = new FaqTableEntity(UUID.randomUUID(),new Date(),answer,question,null);

        return faqRepository.insert(faq);


    }

    public boolean deleteFaq(FaqTableEntity faqTableEntity){
        try {
            faqRepository.delete(faqTableEntity);
        }catch (Exception e){
            throw new CResourceNotExistException();
        }

        return true;
    }

}
