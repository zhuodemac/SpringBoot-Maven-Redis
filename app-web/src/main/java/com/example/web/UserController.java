package com.example.web;

import com.example.common.ApiResponse;
import com.example.dao.entity.UserInfo;
import com.example.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<List<UserInfo>> findAll() {
        return ApiResponse.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<UserInfo> findById(@PathVariable("id") Long id) {
        UserInfo user = userService.findById(id);
        return user == null ? ApiResponse.error("User not found") : ApiResponse.ok(user);
    }

    @PostMapping
    public ApiResponse<UserInfo> create(@Valid @RequestBody UserRequest request) {
        UserInfo user = new UserInfo(null, request.userName(), request.email());
        return ApiResponse.ok(userService.create(user));
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        UserInfo user = new UserInfo(id, request.userName(), request.email());
        return ApiResponse.ok(userService.update(user));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(userService.delete(id));
    }

    public record UserRequest(@NotBlank String userName, @NotBlank @Email String email) {}
}
