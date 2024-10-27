package com.brainzzle.brainzzle_api.dto;

import lombok.Data;

@Data
public class PasswordUpdateDTO {
    private String currentPassword;
    private String newPassword;
}
