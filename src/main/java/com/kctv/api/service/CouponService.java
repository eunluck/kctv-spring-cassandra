package com.kctv.api.service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kctv.api.advice.exception.CPartnerNotFoundException;
import com.kctv.api.advice.exception.CRequiredValueException;
import com.kctv.api.advice.exception.CResourceNotExistException;
import com.kctv.api.model.coupon.CouponEntity;
import com.kctv.api.model.coupon.PartnerByCouponEntity;
import com.kctv.api.model.coupon.UserByCouponDto;
import com.kctv.api.model.coupon.UserByCouponEntity;
import com.kctv.api.model.place.*;
import com.kctv.api.model.stylecard.PartnersByTags;
import com.kctv.api.repository.ap.*;
import com.kctv.api.repository.coupon.CouponRepository;
import com.kctv.api.repository.coupon.PartnerByCouponRepository;
import com.kctv.api.repository.coupon.UserByCouponRepository;
import com.kctv.api.repository.interview.OwnerInterviewRepository;
import com.kctv.api.repository.tag.PlaceTypeRepository;
import com.kctv.api.util.GeoOperations;
import com.kctv.api.util.MapUtill;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final PartnerRepository partnerRepository;
    private final PartnerByCouponRepository partnerByCouponRepository;
    private final CouponRepository couponRepository;
    private final UserByCouponRepository userByCouponRepository;



    public List<CouponEntity> getCouponByCouponId(List<UUID> couponId){


        return couponRepository.findByCouponIdIn(couponId);
    }

    public CouponEntity getCouponByCouponId(UUID couponId){


        return couponRepository.findByCouponId(couponId);
    }


    public List<CouponEntity> getCouponByPlaceId(UUID placeId){

        List<PartnerByCouponEntity> couponEntities = partnerByCouponRepository.findByPartnerId(placeId);
        if (CollectionUtils.isEmpty(couponEntities)){
            return Lists.newArrayList();
        }

        return couponRepository.findByCouponIdIn(couponEntities.stream().map(PartnerByCouponEntity::getCouponId).collect(Collectors.toList())).stream().filter(couponEntity -> couponEntity.isState()).collect(Collectors.toList());

    }

    public List<UserByCouponDto> getCouponByUser(UUID userId){


        return userByCouponRepository.findByUserId(userId).stream().map(UserByCouponDto::new).collect(Collectors.toList());
    }





}
