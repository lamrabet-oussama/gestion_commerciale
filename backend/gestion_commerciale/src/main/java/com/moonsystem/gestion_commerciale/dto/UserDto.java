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

    // Additional fields can be added as needed
    public static UserDto fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return UserDto.builder()
                .cod(user.getCod())
                .username(user.getLogin())
                .role(user.getRole())
                .build();
    }

    public static User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setCod(dto.getCod());
        user.setLogin(dto.getUsername());
        user.setRole(dto.getRole());
        return user;
    }
}
