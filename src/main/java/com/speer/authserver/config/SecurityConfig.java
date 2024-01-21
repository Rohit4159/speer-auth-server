package com.speer.authserver.config;

import com.speer.authserver.util.JwtAuthenticationFilter;
import com.speer.authserver.util.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

//    private final UserDetailsService userDetailsService;
//    public SecurityConfig(UserDetailsService userDetailsService) {
//        this.userDetailsService = userDetailsService;
//    }
    private final AuthenticationConfiguration authConfiguration;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, AuthenticationConfiguration authConfiguration) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authConfiguration = authConfiguration;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 20)
    public SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize->
                authorize
                        .requestMatchers(antMatcher("/api/auth/**"),antMatcher("/api/auth/login")).permitAll()
                        .anyRequest().authenticated());

        http.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

//    public UserDetailsService userDetailsService() {
//        var u1 = User.withUsername("user")
//                .password("password")
//                .authorities("read")
//                .build();
//
//        return new InMemoryUserDetailsManager(u1);
//    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

}