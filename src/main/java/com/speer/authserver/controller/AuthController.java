package com.speer.authserver.controller;

import com.speer.authserver.model.LoginRequest;
import com.speer.authserver.model.SignUpRequest;
import com.speer.authserver.model.User;
import com.speer.authserver.repository.UserRepository;
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
    public ResponseEntity<String> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        log.info("JWT Token: {}",jwt);
        return new ResponseEntity<String>("Login success",HttpStatus.ACCEPTED);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
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
    }
}

