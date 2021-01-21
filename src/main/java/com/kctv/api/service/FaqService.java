package com.kctv.api.service;

import com.google.common.base.Preconditions;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.entity.admin.FaqTable;
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

    public List<FaqTable> findByAll(){

        return faqRepository.findAll();
    }


    public FaqTable findById(UUID uuid){
        return faqRepository.findByFaqId(uuid).orElseThrow(CResourceNotExistException::new);
    }

    public FaqTable faqSaveOrUpdate(FaqTable faqTable){
        return Optional.ofNullable(faqRepository.save(faqTable)).orElseThrow(CResourceNotExistException::new);
    }

    public FaqTable postFaq(String question, String answer){
        Preconditions.checkNotNull(question);
        Preconditions.checkNotNull(answer);

        FaqTable faq = new FaqTable(UUID.randomUUID(),new Date(),answer,question,null);
        FaqTable inserted = faqRepository.insert(faq);

        return inserted;


    }

    public boolean deleteFaq(FaqTable faqTable){
        try {
            faqRepository.delete(faqTable);
        }catch (Exception e){
            throw new CResourceNotExistException();
        }

        return true;
    }

}
