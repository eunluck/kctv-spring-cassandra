package com.kctv.api.service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.kctv.api.advice.exception.CResourceNotExistException;

import com.kctv.api.entity.admin.QnaAnswer;
import com.kctv.api.entity.qna.QnaByUserEntity;
import com.kctv.api.model.qna.QnaDto;
import com.kctv.api.model.qna.QnaRequest;
import com.kctv.api.repository.qna.QnaAnswerRepository;
import com.kctv.api.repository.qna.QnaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@AllArgsConstructor
@Service
public class QnaService {

    private final QnaAnswerRepository qnaAnswerRepository;
    private final QnaRepository qnaRepository;





    public QnaByUserEntity findQuestion(UUID questionId){


        return qnaRepository.findByQuestionId(questionId).orElseThrow(CResourceNotExistException::new);
    }

    public QnaByUserEntity modifyQuestion(QnaByUserEntity qnaRequest){

        return qnaRepository.save(qnaRequest);
    }

    public QnaByUserEntity postQuestion(QnaRequest question, UUID userId,String Nickname, String email) {
        Preconditions.checkNotNull(question);
        Preconditions.checkNotNull(userId);

        if(question.getQuestionType().equals("as")){
            question.setQuestionType("WiFi A/S");
        }else if(question.getQuestionType().equals("zone")){
            question.setQuestionType("WiFi Zone");
        }else if(question.getQuestionType().equals("app")){
            question.setQuestionType("WakeUf 앱 문의");
        }else if(question.getQuestionType().equals("etc")){
            question.setQuestionType("기타 문의");
        }
        question.setStatus("미답변");

        QnaByUserEntity inserted = qnaRepository.insert(new QnaByUserEntity(question,userId,Nickname,email));

        return inserted;
    }


    public QnaAnswer createAnswer(QnaAnswer qnaAnswer){

        QnaByUserEntity qna = qnaRepository.findByUserIdAndQuestionId(qnaAnswer.getUserId(),qnaAnswer.getQuestionId()).orElseThrow(CResourceNotExistException::new);
        qna.setStatus("답변완료");

        Optional.ofNullable(qnaRepository.save(qna)).orElseThrow(CResourceNotExistException::new);

        return Optional.ofNullable(qnaAnswerRepository.save(qnaAnswer)).orElseThrow(CResourceNotExistException::new);
    }


    public QnaAnswer saveOrInsertAnswer(QnaAnswer qnaAnswer){

        return Optional.ofNullable(qnaAnswerRepository.save(qnaAnswer)).orElseThrow(CResourceNotExistException::new);
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
                        .remark(qnaEntity.getRemark())
                        .status(qnaEntity.getStatus())
                        .userId(qnaEntity.getUserId())
                        .questionId(qnaEntity.getQuestionId())
                        .userNickname(qnaEntity.getUserNickname())
                        .userEmail(qnaEntity.getUserEmail())
                        .answers(qnaAnswerRepository.findByQuestionId(qnaEntity.getQuestionId()))
                        .build())
                .orElseThrow(CResourceNotExistException::new);

    }
}
