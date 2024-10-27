package com.brainzzle.brainzzle_api.controllers;

import com.brainzzle.brainzzle_api.dto.PasswordUpdateDTO;
import com.brainzzle.brainzzle_api.dto.ReqRes;
import com.brainzzle.brainzzle_api.dto.UserUpdateDTO;
import com.brainzzle.brainzzle_api.entities.User;
import com.brainzzle.brainzzle_api.services.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/auth/register")
    public ResponseEntity<ReqRes> register(@RequestBody ReqRes request) {
        ReqRes user = userService.register(request);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (user.getStatusCode() != 200) {
            return ResponseEntity.status(user.getStatusCode()).body(user);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ReqRes> login(@RequestBody ReqRes request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes request) {
        return ResponseEntity.ok(userService.refreshToken(request));
    }

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<ReqRes> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/admin/get-user/{userId}")
    public ResponseEntity<ReqRes> getUserByID(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUsersById(userId));
    }

    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable Long userId, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(userId, user));
    }

    @PutMapping("/user/update-password/{userId}")
    public ResponseEntity<ReqRes> updatePassword(
            @PathVariable Long userId,
            @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        ReqRes response = userService.updatePassword(userId, passwordUpdateDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/user/update-details")
    public ResponseEntity<ReqRes> updateDetails(
            @RequestBody UserUpdateDTO userUpdateDTO) {
        ReqRes response = userService.updateDetails(userUpdateDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/adminuser/get-profile")
    public ResponseEntity<ReqRes> getMyProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ReqRes response = userService.getMyInfo(email);
        return  ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<ReqRes> deleteUser(@PathVariable Long userId){
        return ResponseEntity.ok(userService.deleteUser(userId));
    }

    @DeleteMapping("/user/delete/{userId}")
    public ResponseEntity<ReqRes> deleteMyUser(@PathVariable Long userId){
        return ResponseEntity.ok(userService.deleteUser(userId));
    }
}
