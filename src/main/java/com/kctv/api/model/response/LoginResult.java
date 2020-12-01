package com.kctv.api.model.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResult<T> extends CommonResult{
    private String token;
    private Boolean emailVerify;
    private T data;
}
