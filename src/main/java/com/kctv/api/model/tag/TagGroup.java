package com.kctv.api.model.tag;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public enum TagGroup {

    tag1("컬러", Arrays.asList("뉴트럴","따뜻한","블랙","시원한","화이트"),1L),
    tag2("직업",Arrays.asList("간호사","개발자","건설업","교사","내일 없이 노는","디자이너","사무직","서비스업","인생 설계 중","자영업"),1L),
    tag3("가족구성",Arrays.asList("가족","반려동물","싱글","애기아빠","애기엄마","유부남","유부녀","커플"),1L),
    tag4("카테고리",Arrays.asList("건강","뷰티","신상"),2L),
    tag5("현재상태",Arrays.asList("제주생활","제주여행"),5L),
    tag6("테그종류",Arrays.asList("컬러","직업","가족구성","카테고리","현재상태"),5L),
    etc("신규", Lists.newArrayList(),1L);

    private String tagType;
    private List<String> tagName;
    private long point;

    TagGroup(String tagType, List<String> tagName, long point) {
        this.tagType = tagType;
        this.tagName = tagName;
        this.point = point;
    }

    public boolean hasTagName(String tag){
        return tagName.stream().anyMatch(tagVal -> tagVal.equals(tag));
    }

    public static long findByTagPoint(String tag){

        return Arrays.stream(TagGroup.values()).filter(tagGroup -> tagGroup.hasTagName(tag)).findAny().orElse(etc).point;
    }



}


