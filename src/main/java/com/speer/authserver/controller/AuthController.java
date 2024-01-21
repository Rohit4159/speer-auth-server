package com.speer.authserver.controller;

import com.speer.authserver.model.LoginRequest;
import com.speer.authserver.model.SignUpRequest;
import com.speer.authserver.model.User;
import com.speer.authserver.repository.UserRepository;
import com.speer.authserver.util.AuthResponse;
import com.speer.authserver.util.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, JwtTokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        AuthResponse response = new AuthResponse();
        String jwtToken = null;
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            jwtToken = tokenProvider.generateToken(authentication);
        } catch (Exception e) {
            log.info("Exception occurred: ", e);
            response.setErrors(Collections.singletonList("User does not exist!!"));
            response.setResult("failure");
            response.setToken(jwtToken);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        response.setResult("success");
        response.setToken(jwtToken);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(signUpRequest.getUsername());

            if (userOptional.isPresent()) {
                return new ResponseEntity<>( "Username is already taken!", HttpStatus.BAD_REQUEST);
            }

            // Create user account
            String hashPassword = passwordEncoder.encode(signUpRequest.getPassword());
            User user = new User(signUpRequest.getName(), signUpRequest.getUsername(), hashPassword,
                    signUpRequest.getEmail(), signUpRequest.getAuthorities());

            userRepository.save(user);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath().path("/api/users/{username}")
                    .buildAndExpand(user.getUsername()).toUri();

            return new ResponseEntity<>("User registered successfully", HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.info("Exception occurred: ", e);
            return new ResponseEntity<>("{\n  status: 503\n  error: \"Internal server error\"\n}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

