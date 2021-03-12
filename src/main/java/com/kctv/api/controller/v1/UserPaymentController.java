

package com.kctv.api.controller.v1;


import com.kctv.api.model.payment.PaymentInfoEntity;
import com.kctv.api.model.user.UserInfoPayDto;
import com.kctv.api.model.ap.WakeupPermissionEntity;
import com.kctv.api.model.payment.SubscribeRequest;
import com.kctv.api.model.response.SingleResult;
import com.kctv.api.service.PaymentService;
import com.kctv.api.service.ResponseService;
import com.kctv.api.service.UserService;
import com.kctv.api.service.WakeupPermissionService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final WakeupPermissionService wakeupPermissionService;

    @PostMapping(value = "")
    public SingleResult<?> subscribeService(@RequestBody SubscribeRequest subscribeRequest){


        PaymentInfoEntity resultPay = paymentService.subscribe(subscribeRequest.getUserId(),subscribeRequest.getAppPaymentCode());
        WakeupPermissionEntity resultPermission =  wakeupPermissionService.findPermissionByUserId(resultPay.getUserId());


        return responseService.getSingleResult(new UserInfoPayDto(userService.findByUserId(resultPay.getUserId()),resultPermission,resultPay));
    }


}
