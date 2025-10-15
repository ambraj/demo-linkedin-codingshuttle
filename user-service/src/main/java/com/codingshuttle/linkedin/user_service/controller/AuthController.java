package com.codingshuttle.linkedin.user_service.controller;

import com.codingshuttle.linkedin.user_service.dto.LoginRequestDto;
import com.codingshuttle.linkedin.user_service.dto.SignupRequestDto;
import com.codingshuttle.linkedin.user_service.dto.UserDto;
import com.codingshuttle.linkedin.user_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final ModelMapper modelMapper;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupRequestDto signupRequestDto) {
        UserDto userDto = authService.signup(signupRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto) {
        String jwtToken = authService.login(loginRequestDto);
        return ResponseEntity.ok(jwtToken);
    }

}
