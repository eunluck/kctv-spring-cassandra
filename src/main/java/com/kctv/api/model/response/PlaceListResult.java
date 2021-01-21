package com.kctv.api.model.response;

import com.kctv.api.entity.place.PlaceInfo;
import com.kctv.api.entity.stylecard.StyleCardInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlaceListResult extends CommonResult{
    private StyleCardInfo data;
    private List<PlaceInfo> placeList;
}
