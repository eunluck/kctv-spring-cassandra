package com.kctv.api.service;

import com.kctv.api.model.place.PlaceInfoEntity;
import com.kctv.api.model.stylecard.StyleCardInfoEntity;
import com.kctv.api.model.response.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResponseService {

    // enum으로 api 요청 결과에 대한 code, message를 정의합니다.
    public enum CommonResponse {
        SUCCESS(0, "성공하였습니다.");

        int code;
        String msg;

        CommonResponse(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
    // 단일건 결과를 처리하는 메소드
    public <T> SingleResult<T> getSingleResult(T data) {
        SingleResult<T> result = new SingleResult<>();
        result.setData(data);
        setSuccessResult(result);
        return result;
    }
    // 로그인 결과를 처리하는 메서드
    public <T> LoginResult<T> getLoginResult(T user){
        LoginResult<T> result = new LoginResult<>();
        result.setData(user);
        setSuccessResult(result);
        return result;
    }


    public PlaceListResult getPlaceListResult(StyleCardInfoEntity cardInfo, List<PlaceInfoEntity> placeInfoEntityList){
        PlaceListResult result = new PlaceListResult();
        result.setData(cardInfo);
        result.setPlaceList(placeInfoEntityList);
        setSuccessResult(result);
        return result;

    }


    // 다중건 결과를 처리하는 메소드
    public <T> ListResult<T> getListResult(List<T> list) {
        ListResult<T> result = new ListResult<>();
        result.setList(list);
        setSuccessResult(result);
        return result;
    }
    // 성공 결과만 처리하는 메소드
    public CommonResult getSuccessResult() {
        CommonResult result = new CommonResult();
        setSuccessResult(result);
        return result;
    }
    // 실패 결과만 처리하는 메소드
    public CommonResult getFailResult(int code, String msg) {
        CommonResult result = new CommonResult();
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(msg);
        return result;
    }
    // 결과 모델에 api 요청 성공 데이터를 세팅해주는 메소드
    private void setSuccessResult(CommonResult result) {
        result.setSuccess(true);
        result.setCode(CommonResponse.SUCCESS.getCode());
        result.setMessage(CommonResponse.SUCCESS.getMsg());
    }
}
