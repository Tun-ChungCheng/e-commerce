package com.iancheng.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests()
                .antMatchers(HttpMethod.GET, "/products/**", "/products**").permitAll()
                .antMatchers(HttpMethod.POST, "/users/login", "/users/register").permitAll()
                .antMatchers(HttpMethod.GET, "/users/*/orders").permitAll()
                .antMatchers(HttpMethod.POST, "/users/*/orders").permitAll()
                .antMatchers("/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
            .formLogin();

        return http.build();
    }
}
