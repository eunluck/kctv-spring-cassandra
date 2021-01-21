package com.kctv.api.entity.admin;


import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table("faq_table")
public class FaqTable {

    @ApiModelProperty(notes = "faq ID")
    @PrimaryKeyColumn(value = "faq_id",type = PrimaryKeyType.PARTITIONED,ordinal = 0)
    private UUID faqId;
    @PrimaryKeyColumn(value = "create_dt",type = PrimaryKeyType.CLUSTERED,ordinal = 1)
    private Date createDt;
    @ApiModelProperty(notes = "답변")
    private String answer;
    @ApiModelProperty(notes = "질문")
    private String question;
    @Column("modify_dt")
    private Date modifyDt;


}
