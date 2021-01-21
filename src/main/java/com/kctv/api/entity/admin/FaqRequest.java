package com.kctv.api.entity.admin;


import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.util.Date;
import java.util.UUID;


public class FaqRequest {

    @ApiModelProperty(required = true,notes = "답변 내용")
    private String answer;
    @ApiModelProperty(required = true,notes = "질문(제목)")
    private String question;


    public FaqRequest(){}


    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("answer",answer).append("question",question).toString();
    }
}
