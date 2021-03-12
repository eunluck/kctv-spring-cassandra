
package com.kctv.api.model.admin;

import com.kctv.api.model.user.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;


@Data
@AllArgsConstructor
@Builder
public class AdminPaymentInfoEntity {

    public String paymentName;
    public Long price;
    public Date startDate;
    public Date endDate;
    public String paymentType;
    public String referFriend;
    public String status;

}
