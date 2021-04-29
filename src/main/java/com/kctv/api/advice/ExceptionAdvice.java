package com.kctv.api.advice;


import com.kctv.api.advice.exception.*;
import com.kctv.api.model.response.CommonResult;
import com.kctv.api.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ResponseService responseService;

    private final MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult defaultException(HttpServletRequest request, Exception e) {
        log.warn("알수없는 오류 발생: {}", e.getMessage(), e);
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        return responseService.getFailResult(Integer.parseInt(getMessage("unKnown.code")), getMessage("unKnown.msg") + "(" + e.getMessage() + ")");
    }

    @ExceptionHandler(CUserNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult userNotFound(HttpServletRequest request, CUserNotFoundException e) {
        log.info(getMessage("userNotFound.msg")+"::{}", e.getMessage(), e);
        return responseService.getFailResult(Integer.parseInt(getMessage("userNotFound.code")), getMessage("userNotFound.msg"));
    }

    @ExceptionHandler(CEmailSigninFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult emailSigninFailed(HttpServletRequest request, CEmailSigninFailedException e) {
        log.info(getMessage("emailSigninFailed.msg")+"::{}", e.getMessage(), e);
        return responseService.getFailResult(Integer.parseInt(getMessage("emailSigninFailed.code")), getMessage("emailSigninFailed.msg"));
    }

    @ExceptionHandler(CAuthenticationEntryPointException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResult authenticationEntryPointException(HttpServletRequest request, CAuthenticationEntryPointException e) {
        //log.info(getMessage("entryPointException.msg")+"::{}", e.getMessage(), e);
        return responseService.getFailResult(Integer.parseInt(getMessage("entryPointException.code")), getMessage("entryPointException.msg"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public CommonResult accessDeniedException(HttpServletRequest request, AccessDeniedException e) {
        log.info(getMessage("accessDenied.msg")+"::{}", e.getMessage(), e);
        return responseService.getFailResult(Integer.parseInt(getMessage("accessDenied.code")), getMessage("accessDenied.msg"));
    }

    @ExceptionHandler(CCommunicationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult communicationException(HttpServletRequest request, CCommunicationException e) {
        log.info(getMessage("communicationError.msg")+"::{}", e.getMessage(), e);
        return responseService.getFailResult(Integer.parseInt(getMessage("communicationError.code")), getMessage("communicationError.msg"));
    }

    @ExceptionHandler(CUserExistException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult communicationException(HttpServletRequest request, CUserExistException e) {
        log.info(getMessage("existingUser.msg")+"::{}", e.getMessage(), e);
        return responseService.getFailResult(Integer.parseInt(getMessage("existingUser.code")), getMessage("existingUser.msg"));
    }

    @ExceptionHandler(CNotOwnerException.class)
    @ResponseStatus(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
    public CommonResult notOwnerException(HttpServletRequest request, CNotOwnerException e) {
        log.info(getMessage("notOwner.msg")+"::{}", e.getMessage(), e);
        return responseService.getFailResult(Integer.parseInt(getMessage("notOwner.code")), getMessage("notOwner.msg"));
    }

    @ExceptionHandler(CResourceNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult resourceNotExistException(HttpServletRequest request, CResourceNotExistException e) {
        log.debug(getMessage("resourceNotExist.msg")+"::{}", e.getMessage(), e);
        return responseService.getFailResult(Integer.parseInt(getMessage("resourceNotExist.code")), getMessage("resourceNotExist.msg"));
    }

    @ExceptionHandler(CForbiddenWordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResult forbiddenWordException(HttpServletRequest request, CForbiddenWordException e) {
        log.debug(getMessage("forbiddenWord.msg")+"::{}", e.getMessage(), e);
        return responseService.getFailResult(Integer.parseInt(getMessage("forbiddenWord.code")), getMessage("forbiddenWord.msg", new Object[]{e.getMessage()}));
    }

    @ExceptionHandler(CNotFoundEmailException.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResult notFoundEmailException(HttpServletRequest request, CNotFoundEmailException e) {
        log.debug(getMessage("emailNotFound.msg")+"::{}", e.getMessage(), e);
        return responseService.getFailResult(Integer.parseInt(getMessage("emailNotFound.code")), getMessage("emailNotFound.msg"));
    }

    @ExceptionHandler(CIncorrectPasswordException.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResult incorrectPasswordException(HttpServletRequest request, CIncorrectPasswordException e) {
        log.debug(getMessage("incorrectPassword.msg")+"::{}", e.getMessage(), e);
        return responseService.getFailResult(Integer.parseInt(getMessage("incorrectPassword.code")), getMessage("incorrectPassword.msg"));
    }
    @ExceptionHandler(COverlapSnsKey.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResult overlapSnsKey(HttpServletRequest request, COverlapSnsKey e) {
        log.debug(getMessage("overlapSnsKey.msg")+"::{}", e.getMessage(), e);
        return responseService.getFailResult(Integer.parseInt(getMessage("overlapSnsKey.code")), getMessage("overlapSnsKey.msg"));
    }

    @ExceptionHandler(CPartnerNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResult partnerNotFound(HttpServletRequest request, CPartnerNotFoundException e) {
        log.debug(getMessage("partnerNotFound.msg")+"::{}", e.getMessage(), e);
        return responseService.getFailResult(Integer.parseInt(getMessage("partnerNotFound.code")), getMessage("partnerNotFound.msg"));
    }

    @ExceptionHandler(CNotVerifyEmailException.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResult notVerifyEmailException(HttpServletRequest request, CNotVerifyEmailException e) {
        log.debug(getMessage("notVerifyEmailException.msg")+"::{}", e.getMessage(), e);
        return responseService.getFailResult(Integer.parseInt(getMessage("notVerifyEmailException.code")), getMessage("notVerifyEmailException.msg"));
    }

    @ExceptionHandler(CFormatNotAllowedException.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResult formatNotAllowedException(HttpServletRequest request, CFormatNotAllowedException e) {
        log.debug(getMessage("formatNotAllowedException.msg")+"::{}", e.getMessage(), e);
        return responseService.getFailResult(Integer.parseInt(getMessage("formatNotAllowedException.code")), getMessage("formatNotAllowedException.msg"));
    }

   @ExceptionHandler(CTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResult tokenNotFoundException(HttpServletRequest request, CTokenNotFoundException e) {
        log.debug(getMessage("tokenNotFoundException.msg")+"::{}", e.getMessage(), e);
        return responseService.getFailResult(Integer.parseInt(getMessage("tokenNotFoundException.code")), getMessage("tokenNotFoundException.msg"));
    }
    @ExceptionHandler(CNotFoundCodeException.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResult notFoundCodeException(HttpServletRequest request, CNotFoundCodeException e) {
        log.debug(getMessage("notFoundCodeException.msg")+"::{}", e.getMessage(), e);
        return responseService.getFailResult(Integer.parseInt(getMessage("notFoundCodeException.code")), getMessage("notFoundCodeException.msg"));
    }
    @ExceptionHandler(CRequiredValueException.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResult requiredValueException(HttpServletRequest request, CRequiredValueException e) {
        log.debug(getMessage("notFoundCodeException.msg")+"::{}", e.getMessage(), e);
        return responseService.getFailResult(-1004, e.getMessage());
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
