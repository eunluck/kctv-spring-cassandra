/*
package com.kctv.api.model.tag;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

public enum FacilitiesGroup {

    Facilities(Arrays.asList("단체석","주차","포","시원한","화이트"),1L),
    etc("신규", Lists.newArrayList(),1L);

    private String tagType;
    private List<String> tagName;
    private long point;

    FacilitiesGroup(String tagType, List<String> tagName, long point) {
        this.tagType = tagType;
        this.tagName = tagName;
        this.point = point;
    }

    public boolean hasTagName(String tag){
        return tagName.stream().anyMatch(tagVal -> tagVal.equals(tag));
    }

    public static long findByTagPoint(String tag){

        return Arrays.stream(FacilitiesGroup.values()).filter(tagGroup -> tagGroup.hasTagName(tag)).findAny().orElse(etc).point;
    }



}


*/
