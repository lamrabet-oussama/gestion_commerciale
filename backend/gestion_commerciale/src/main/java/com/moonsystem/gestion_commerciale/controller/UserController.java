package com.moonsystem.gestion_commerciale.controller;

import java.util.List;

import com.moonsystem.gestion_commerciale.dto.UpdateUserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Override
    public ResponseEntity<String> bloquerUser(Integer userId){
        userService.bloquerDebloquerUser(userId, false);
        return ResponseEntity.ok("Utilisateur bloqué avec succès");
    }

    @Override
    public ResponseEntity<String> debloquerUser(Integer userId){
        userService.bloquerDebloquerUser(userId, true);
        return ResponseEntity.ok("Utilisateur activé avec succès");
    }

    @Override
   public UserDto getCurrentUser(){
        return userService.getCurrentUser();
    }

    @Override
    public ResponseEntity<UserDto> updateUser(Integer userId, UpdateUserRequest request){
        UserDto updatedUser = userService.updateUser(userId, request);
        return ResponseEntity.ok(updatedUser);
    }
}
