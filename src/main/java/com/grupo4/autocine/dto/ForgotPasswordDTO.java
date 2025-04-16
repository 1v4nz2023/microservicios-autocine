package com.grupo4.autocine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ForgotPasswordDTO {
    @Schema(description = "Email of the user who forgot their password", required = true)
    private String email;
} 