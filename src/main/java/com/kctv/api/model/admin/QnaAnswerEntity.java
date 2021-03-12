package com.kctv.api.model.admin;


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
    private String title;
    @ApiModelProperty(required = true)
    private String content;
    @ApiModelProperty
    private String remark;
    @Column("user_id")
    @ApiModelProperty(hidden = true)
    private UUID userId;
    @ApiModelProperty(hidden = true)
    private String status;
    @ApiModelProperty(hidden = true)
    @Column("create_dt")
    private Date createDt;


}
