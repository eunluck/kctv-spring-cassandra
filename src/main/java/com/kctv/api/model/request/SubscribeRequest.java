package com.kctv.api.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SubscribeRequest {


    @ApiModelProperty(value = "구독자 ID", required = true)
    private UUID userId;
    @ApiModelProperty(value = "구독 상품 코드", required = true)
    private String appPaymentCode;

}
