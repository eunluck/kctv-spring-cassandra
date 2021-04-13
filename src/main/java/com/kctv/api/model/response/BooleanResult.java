package com.kctv.api.model.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BooleanResult<T> extends CommonResult{

    private boolean data;
    private T content;

}
