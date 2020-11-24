package com.kctv.api.advice;


import com.kctv.api.advice.exception.*;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    private final ResponseService responseService;

    private final MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult defaultException(HttpServletRequest request, Exception e) {
        e.printStackTrace();
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        return responseService.getFailResult(Integer.valueOf(getMessage("unKnown.code")), getMessage("unKnown.msg") + "(" + e.getMessage() + ")");
    }

    @ExceptionHandler(CUserNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult userNotFound(HttpServletRequest request, CUserNotFoundException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("userNotFound.code")), getMessage("userNotFound.msg"));
    }

    @ExceptionHandler(CEmailSigninFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult emailSigninFailed(HttpServletRequest request, CEmailSigninFailedException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("emailSigninFailed.code")), getMessage("emailSigninFailed.msg"));
    }

    @ExceptionHandler(CAuthenticationEntryPointException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResult authenticationEntryPointException(HttpServletRequest request, CAuthenticationEntryPointException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("entryPointException.code")), getMessage("entryPointException.msg"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public CommonResult accessDeniedException(HttpServletRequest request, AccessDeniedException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("accessDenied.code")), getMessage("accessDenied.msg"));
    }

    @ExceptionHandler(CCommunicationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult communicationException(HttpServletRequest request, CCommunicationException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("communicationError.code")), getMessage("communicationError.msg"));
    }

    @ExceptionHandler(CUserExistException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult communicationException(HttpServletRequest request, CUserExistException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("existingUser.code")), getMessage("existingUser.msg"));
    }

    @ExceptionHandler(CNotOwnerException.class)
    @ResponseStatus(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
    public CommonResult notOwnerException(HttpServletRequest request, CNotOwnerException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("notOwner.code")), getMessage("notOwner.msg"));
    }

    @ExceptionHandler(CResourceNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult resourceNotExistException(HttpServletRequest request, CResourceNotExistException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("resourceNotExist.code")), getMessage("resourceNotExist.msg"));
    }

    @ExceptionHandler(CForbiddenWordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResult forbiddenWordException(HttpServletRequest request, CForbiddenWordException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("forbiddenWord.code")), getMessage("forbiddenWord.msg", new Object[]{e.getMessage()}));
    }

    @ExceptionHandler(CNotFoundEmailException.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResult notFoundEmailException(HttpServletRequest request, CNotFoundEmailException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("emailNotFound.code")), getMessage("emailNotFound.msg"));
    }

    @ExceptionHandler(CIncorrectPasswordException.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResult incorrectPasswordException(HttpServletRequest request, CIncorrectPasswordException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("incorrectPassword.code")), getMessage("incorrectPassword.msg"));
    }
    @ExceptionHandler(COverlapSnsKey.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResult overlapSnsKey(HttpServletRequest request, COverlapSnsKey e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("overlapSnsKey.code")), getMessage("overlapSnsKey.msg"));
    }

    @ExceptionHandler(CPartnerNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResult partnerNotFound(HttpServletRequest request, CPartnerNotFoundException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("partnerNotFound.code")), getMessage("partnerNotFound.msg"));
    }

    @ExceptionHandler(CNotVerifyEmailException.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResult notVerifyEmailException(HttpServletRequest request, CNotVerifyEmailException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("notVerifyEmailException.code")), getMessage("notVerifyEmailException.msg"));
    }




    // code정보에 해당하는 메시지를 조회합니다.
    private String getMessage(String code) {
        return getMessage(code, null);
    }

    // code정보, 추가 argument로 현재 locale에 맞는 메시지를 조회합니다.
    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
