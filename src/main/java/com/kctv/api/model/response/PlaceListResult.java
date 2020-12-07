package com.kctv.api.model.response;

import com.kctv.api.entity.ap.PartnerInfo;
import com.kctv.api.entity.tag.StyleCardInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlaceListResult extends CommonResult{
    private StyleCardInfo data;
    private List<PartnerInfo> placeList;
}
