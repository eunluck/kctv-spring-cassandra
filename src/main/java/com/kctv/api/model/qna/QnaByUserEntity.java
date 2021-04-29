package com.kctv.api.model.qna;




import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.kctv.api.model.qna.QnaRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.springframework.beans.BeanUtils.copyProperties;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table("qna_by_user")
public class QnaByUserEntity {

    @PrimaryKeyColumn(value = "user_id",type = PrimaryKeyType.PARTITIONED,ordinal = 0)
    private UUID userId;
    @PrimaryKeyColumn(value = "question_id",type = PrimaryKeyType.CLUSTERED,ordering = Ordering.DESCENDING,ordinal = 1)
    private UUID questionId;
    private String content;
    @Column("create_dt")
    private Date createDt;
    @Column("modify_dt")
    private Date modifyDt;
    private Long latitude;
    private Long longitude;
    private String address;
    @Column("question_type")
    private String questionType;
    private String status;
    private String title;
    @Column("user_nickname")
    private String userNickname;
    @Column("user_email")
    private String userEmail;
    //private String remark;

    public QnaByUserEntity(QnaRequest qnaRequest,UUID userId,String userNickname, String userEmail){
        copyProperties(qnaRequest,this);
        this.userId = userId;
        this.createDt = new Date();
        this.questionId = Uuids.timeBased();
        this.userNickname = userNickname;
        this.userEmail = userEmail;

    }
    public QnaByUserEntity(QnaRequest qnaRequest){
        copyProperties(qnaRequest,this);
        this.modifyDt = new Date();

    }


}
