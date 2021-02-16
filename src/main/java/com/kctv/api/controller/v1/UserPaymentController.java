

package com.kctv.api.controller.v1;


import com.kctv.api.entity.payment.PaymentInfo;
import com.kctv.api.entity.user.UserInfoDto;
import com.kctv.api.model.request.SubscribeRequest;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.PaymentService;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@Api(tags = {"17. User Payment API"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/payment")
public class UserPaymentController {

    private final UserService userService;
    private final PaymentService paymentService;
    private final ResponseService responseService;


    @PostMapping(value = "/")
    public SingleResult<?> subscribeService(SubscribeRequest subscribeRequest){





        return responseService.getSingleResult(paymentService.subscribe(subscribeRequest.getUserId(),subscribeRequest.getAppPaymentCode()));
    }


}
