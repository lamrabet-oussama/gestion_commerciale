package com.moonsystem.gestion_commerciale.services;

import java.util.List;

import com.moonsystem.gestion_commerciale.dto.UpdateUserRequest;
import com.moonsystem.gestion_commerciale.dto.UserDto;

public interface UserService {

    List<UserDto> getAllUsers();
    UserDto getCurrentUser();
    void bloquerDebloquerUser(Integer userId, boolean etat);
    UserDto updateUser(Integer userId, UpdateUserRequest request);
}
