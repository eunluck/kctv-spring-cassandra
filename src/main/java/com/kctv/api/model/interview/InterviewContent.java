package com.kctv.api.model.interview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@UserDefinedType("interview_content")
public class InterviewContent {
    @Column("content_order")
    private int contentOrder;
    @Column("content_type")
    private String contentType;
    private String content;

}
