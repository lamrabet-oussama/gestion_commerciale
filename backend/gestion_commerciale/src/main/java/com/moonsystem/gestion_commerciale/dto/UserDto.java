package com.moonsystem.gestion_commerciale.dto;

import com.moonsystem.gestion_commerciale.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Integer cod;
    private String username;
    private String role;
    private String gsm;
    private boolean etat=true;
    // Additional fields can be added as needed
    public static UserDto fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return UserDto.builder()
                .cod(user.getCod())
                .role(user.getRole().name())
                .username(user.getLogin())
                .etat(user.isEnabled())
                .gsm(user.getGsm())
                .build();
    }


}
