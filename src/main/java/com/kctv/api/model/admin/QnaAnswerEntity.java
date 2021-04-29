package com.kctv.api.model.admin;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

@Data
@Table("qna_answer")
public class QnaAnswerEntity {

    @PrimaryKeyColumn(value = "question_id",type = PrimaryKeyType.PARTITIONED,ordinal = 0)
    @ApiModelProperty(required = true)
    private UUID questionId;
    @PrimaryKeyColumn(value = "answer_id",type = PrimaryKeyType.CLUSTERED,ordinal = 1,ordering = Ordering.DESCENDING)
    @ApiModelProperty(hidden = true)
    private UUID answerId;
    @ApiModelProperty(required = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String title;
    @ApiModelProperty(required = true)
    private String content;
    //@ApiModelProperty
    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    //private String remark;
    @Column("user_id")
    @ApiModelProperty(hidden = true)
    private UUID userId;
    @ApiModelProperty(hidden = true,example = "접수,처리중,처리완료")
    private String status;
    @ApiModelProperty(hidden = true)
    @Column("create_dt")
    private Date createDt;


    public void modify(String content,String status){

        if (!Strings.isNullOrEmpty(content))
        this.content = content;
        if (!Strings.isNullOrEmpty(status))
        this.status = status;
    }


}
