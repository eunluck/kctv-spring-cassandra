package com.kctv.api.service;

import com.kctv.api.advice.exception.CPartnerNotFoundException;
import com.kctv.api.common.GeoOperations;
import com.kctv.api.entity.ap.WifiInfo;
import com.kctv.api.repository.ap.WifiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlaceService {

    final WifiRepository wifiRepository;

    public List<WifiInfo> getPartnerWifiService(UUID partnerId, double distance){

        WifiInfo wifiInfo = wifiRepository.findByPartnerId(partnerId).orElseThrow(CPartnerNotFoundException::new);

        GeoOperations geo = new GeoOperations(wifiInfo.getApLat(),wifiInfo.getApLon());

        double[] geoArr = geo.GenerateBoxCoordinates(distance);

        return wifiRepository.findByApLatGreaterThanAndApLatLessThanAndApLonGreaterThanAndApLonLessThan(geoArr[1],geoArr[0],geoArr[2],geoArr[3]);


    }



}
