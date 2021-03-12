package com.kctv.api.model.response;

import com.kctv.api.model.place.PlaceInfoEntity;
import com.kctv.api.model.stylecard.StyleCardInfoEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlaceListResult extends CommonResult{
    private StyleCardInfoEntity data;
    private List<PlaceInfoEntity> placeList;
}
