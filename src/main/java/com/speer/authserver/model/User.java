package com.speer.authserver.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@NoArgsConstructor
@Document(collection = "users")
public class User implements UserDetails {

    @Id
    private String userId;
    private String name;
    private String username;
    private String password;
    private String email;
    private List<String> authorities;

    public User(String name, String username, String password, String email, List<String> authorities) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.authorities = authorities;
    }
    public List<String> getAuth() {
        return authorities;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // Implement account expiration logic if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // Implement account locking logic if needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // Implement credentials expiration logic if needed
    }

    @Override
    public boolean isEnabled() {
        return true;  // Implement account activation logic if needed
    }
}
