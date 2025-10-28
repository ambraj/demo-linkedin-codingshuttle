package com.codingshuttle.linkedin.user_service.service;

import com.codingshuttle.linkedin.user_service.dto.LoginRequestDto;
import com.codingshuttle.linkedin.user_service.dto.SignupRequestDto;
import com.codingshuttle.linkedin.user_service.dto.UserDto;
import com.codingshuttle.linkedin.user_service.entity.User;
import com.codingshuttle.linkedin.event.UserCreatedEvent;
import com.codingshuttle.linkedin.user_service.exception.BadRequestException;
import com.codingshuttle.linkedin.user_service.exception.ResourceNotFoundException;
import com.codingshuttle.linkedin.user_service.repository.UserRepository;
import com.codingshuttle.linkedin.user_service.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final KafkaProducerService kafkaProducerService;

    public UserDto signup(SignupRequestDto signupRequestDto) {
        boolean userExists = userRepository.existsByEmail(signupRequestDto.getEmail());
        if (userExists) {
            throw new BadRequestException("Email is already registered");
        }
        User user = modelMapper.map(signupRequestDto, User.class);
        user.setPassword(PasswordUtil.hashPassword(signupRequestDto.getPassword()));
        user = userRepository.save(user);

        // Publish UserCreatedEvent to Kafka
        UserCreatedEvent event = UserCreatedEvent.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
        kafkaProducerService.publishUserCreatedEvent(event);
        log.info("Published UserCreatedEvent for userId: {}", user.getId());

        return modelMapper.map(user, UserDto.class);
    }

    public String login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loginRequestDto.getEmail()));
        if (!PasswordUtil.verifyPassword(loginRequestDto.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }
        return jwtService.generateToken(user);
    }
}
