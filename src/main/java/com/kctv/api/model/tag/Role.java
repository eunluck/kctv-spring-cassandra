
package com.kctv.api.model.tag;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;

import javax.management.relation.RoleNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Role {


    USER(Collections.singletonList("ROLE_USER"), "일반유저"),
    ADMIN(Lists.newArrayList("ROLE_USER", "ROLE_ADMIN", "ROLE_WIRELESS_ADMIN", "ROLE_CUSTOMER_ADMIN"), "관리자"),
    ADMIN_WIRELESS(Lists.newArrayList("ROLE_WIRELESS_ADMIN","ROLE_USER"), "무선사업국"),
    ADMIN_CUSTOMER(Lists.newArrayList("ROLE_CUSTOMER_ADMIN","ROLE_USER"), "고객감동실"),
    ADMIN_AP(Lists.newArrayList("ROLE_AP_ADMIN","ROLE_USER"), "기술국"),
    EMAIL_NOT_VERIFY(Lists.newArrayList("ROLE_NOT_VERIFY_EMAIL"), "이메일 미인증"),
    NOT_ROLE(Lists.newArrayList("ROLE_EMPTY"), "권한없음");

    private List<String> authority;
    private String description;

    Role(List<String> authority, String description) {
        this.authority = authority;
        this.description = description;
    }

    public List<String> getAuthority() {

        return this.authority;
    }

    public static Role findAuthorityByDescription(String description) throws RoleNotFoundException {

        return Arrays.stream(Role.values())
                .filter(role -> role.description.equals(description))
                .findAny()
                .orElseThrow(() -> new RoleNotFoundException("존재하지 않는 권한입니다."));
    }


    public static String findDescriptionByAuthority(List<String> authority) {

        return Arrays.stream(Role.values()).filter(role -> authority.containsAll(role.authority) && role.authority.size() == authority.size()).findAny().orElseGet(() -> NOT_ROLE).description;

    }

    public static boolean AdminIsTrue(List<String> role){

        return !findDescriptionByAuthority(role).equals(USER.description)
                &&!findDescriptionByAuthority(role).equals(NOT_ROLE.description)
                &&!findDescriptionByAuthority(role).equals(EMAIL_NOT_VERIFY.description);

    }


}

