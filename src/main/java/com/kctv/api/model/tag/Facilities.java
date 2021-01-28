
package com.kctv.api.model.tag;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

public enum Facilities {

    facilities("편의시설",Arrays.asList("단체석","주차","발레파킹","포장","배달","방문접수/출장","예약","무선인터넷","반려동물 동반","유아시설(놀이방)","장애인 편의시설"));

    private String tagType;
    private List<String> facilitiesName;


    Facilities(String tagType,List<String> facilitiesName) {
        this.tagType = tagType;
        this.facilitiesName = facilitiesName;
    }


    public List<String> getFacilitiesName(){

        return facilitiesName;
    }
}

