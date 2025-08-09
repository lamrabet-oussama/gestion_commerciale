package com.moonsystem.gestion_commerciale.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import com.moonsystem.gestion_commerciale.controller.api.UserApi;
import com.moonsystem.gestion_commerciale.dto.UserDto;
import com.moonsystem.gestion_commerciale.services.UserService;

@RestController
public class UserController implements UserApi {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

}
