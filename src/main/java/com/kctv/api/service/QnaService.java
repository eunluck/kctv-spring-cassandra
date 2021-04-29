package com.kctv.api.service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.kctv.api.advice.exception.CResourceNotExistException;

import com.kctv.api.model.admin.QnaAnswerEntity;
import com.kctv.api.model.qna.QnaByUserEntity;
import com.kctv.api.model.qna.QnaDto;
import com.kctv.api.model.qna.QnaRequest;
import com.kctv.api.repository.qna.QnaAnswerRepository;
import com.kctv.api.repository.qna.QnaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@AllArgsConstructor
@Service
public class QnaService {

    private final QnaAnswerRepository qnaAnswerRepository;
    private final QnaRepository qnaRepository;


    public List<QnaAnswerEntity> qnaByQuestionIdList(UUID questionId){

        return qnaAnswerRepository.findByQuestionId(questionId);
    }

    public QnaAnswerEntity findAnswerById(UUID questionId, UUID answerId){
        return qnaAnswerRepository.findByQuestionIdAndAnswerId(questionId,answerId);
    }


    public QnaByUserEntity findQuestion(UUID questionId){


        return qnaRepository.findByQuestionId(questionId).orElseThrow(CResourceNotExistException::new);
    }

    public QnaByUserEntity modifyQuestion(QnaByUserEntity qnaRequest){

        return qnaRepository.save(qnaRequest);
    }

    public QnaByUserEntity postQuestion(QnaRequest question, UUID userId,String Nickname, String email) {
        Preconditions.checkNotNull(question);
        Preconditions.checkNotNull(userId);

        switch (question.getQuestionType()) {
            case "as":
                question.setQuestionType("WiFi A/S");
                break;
            case "zone":
                question.setQuestionType("WiFi Zone");
                break;
            case "app":
                question.setQuestionType("WakeUf 앱 문의");
                break;
            case "payment":
                question.setQuestionType("결제 문의");
                break;
            case "pay":
                question.setQuestionType("결제 문의");
                break;
            case "etc":
                question.setQuestionType("기타 문의");
                break;
        }
        question.setStatus("미접수");

        return qnaRepository.insert(new QnaByUserEntity(question,userId,Nickname,email));
    }


    public QnaAnswerEntity createAnswer(QnaAnswerEntity qnaAnswerEntity){

        /*
        QnaByUserEntity qna = qnaRepository.findByUserIdAndQuestionId(qnaAnswerEntity.getUserId(), qnaAnswerEntity.getQuestionId()).orElseThrow(CResourceNotExistException::new);

        qnaRepository.save(qna);*/

        return qnaAnswerRepository.save(qnaAnswerEntity);
    }


    public QnaAnswerEntity saveOrInsertAnswer(QnaAnswerEntity qnaAnswerEntity){


        return qnaAnswerRepository.save(qnaAnswerEntity);
    }



    public List<QnaByUserEntity> getAllQnaList(String param){
        if(!Strings.isNullOrEmpty(param)){
            return qnaRepository.findByStatus(param);
        }

        return qnaRepository.findAll();
    }

    public List<QnaByUserEntity> getQnaList(UUID userId){

        return qnaRepository.findByUserId(userId);
    }

    public QnaDto getQna(UUID questionId){


        return qnaRepository.findByQuestionId(questionId)
                .map(qnaEntity
                        -> QnaDto.builder()
                        .content(qnaEntity.getContent())
                        .createDt(qnaEntity.getCreateDt())
                        .latitude(qnaEntity.getLatitude())
                        .longitude(qnaEntity.getLongitude())
                        .questionType(qnaEntity.getQuestionType())
                        .modifyDt(qnaEntity.getModifyDt())
                        .title(qnaEntity.getTitle())
                        .status(qnaEntity.getStatus())
                        .address(qnaEntity.getAddress())
                        .userId(qnaEntity.getUserId())
                        .questionId(qnaEntity.getQuestionId())
                        .userNickname(qnaEntity.getUserNickname())
                        .userEmail(qnaEntity.getUserEmail())
                        .answers(qnaAnswerRepository.findByQuestionId(qnaEntity.getQuestionId()).stream().limit(1).findFirst().orElseGet(() -> null))
                        .build())
                .orElseThrow(CResourceNotExistException::new);

    }
}
