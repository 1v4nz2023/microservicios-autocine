package com.grupo4.autocine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ResetPasswordDTO {
    @Schema(description = "Reset token received via email", required = true)
    private String token;

    @Schema(description = "New password", required = true)
    private String newPassword;
} 