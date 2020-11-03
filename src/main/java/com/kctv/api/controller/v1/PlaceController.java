package com.kctv.api.controller.v1;


import com.kctv.api.entity.ap.PartnerInfo;
import com.kctv.api.entity.ap.WifiInfo;
import com.kctv.api.service.PlaceService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Api(tags = {"02. Place"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class PlaceController {

    final PlaceService placeService;

    @GetMapping("/place/wifi/{partnerId}/{distance}")
    public List<WifiInfo> getGeo(@PathVariable("partnerId") UUID partnerUuid, @PathVariable("distance")double distance){

        return placeService.getPartnerWifiService(partnerUuid,distance);

    }

    @GetMapping("/places")
    public List<PartnerInfo> getPlaceAll(){


    return null;

    }



}
