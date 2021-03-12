package com.kctv.api.controller.v1;


import com.kctv.api.advice.exception.CAuthenticationEntryPointException;
import com.kctv.api.advice.exception.CNotVerifyEmailException;
import com.kctv.api.model.user.UserInfoEntity;
import com.kctv.api.model.response.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/exception")
public class ExceptionController {

    @GetMapping("/entrypoint")
    public CommonResult entrypointException(){
        throw new CAuthenticationEntryPointException();

    }

    @GetMapping("/entrypoint/verify")
    public CommonResult notVerifyEmailException(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoEntity user = (UserInfoEntity) authentication.getPrincipal();

        if(user.getRoles().stream().anyMatch(s -> s.contains("NOT_VERIFY_EMAIL"))){

          throw new CNotVerifyEmailException();
        }else {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
    }
}
