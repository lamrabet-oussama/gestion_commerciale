package com.moonsystem.gestion_commerciale.controller.auth;

import com.moonsystem.gestion_commerciale.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String gsm;
    private String password;
    private Role role;
}
