package com.kctv.api.model.user;

import lombok.Data;

@Data
public class PasswordUpdateRequest {

    private String currentPassword;
    private String newPassword;


}
