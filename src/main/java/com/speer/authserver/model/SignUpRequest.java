package com.speer.authserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class SignUpRequest {
    private String name;
    private String username;
    private String email;
    private String password;
    private List<String> authorities;
}
