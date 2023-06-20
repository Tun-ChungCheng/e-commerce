package com.iancheng.ecommerce.config;

import com.iancheng.ecommerce.constant.Role;
import com.iancheng.ecommerce.filter.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    public SecurityConfig(
            JwtAuthFilter jwtAuthFilter,
            AuthenticationProvider authenticationProvider,
            LogoutHandler logoutHandler
    ) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
        this.logoutHandler = logoutHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests()
                .antMatchers(HttpMethod.POST,   "/users/login", "/users/register").permitAll()
                .antMatchers(HttpMethod.GET,    "/products/**").permitAll()
                .antMatchers(HttpMethod.POST,   "/products").hasAuthority  (Role.ADMIN.name())
                .antMatchers(HttpMethod.PUT,    "/products/*").hasAuthority(Role.ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/products/*").hasAuthority(Role.ADMIN.name())
                .antMatchers(HttpMethod.GET,    "/users/*/orders").hasAnyAuthority(Role.MEMBER.name(), Role.ADMIN.name())
                .antMatchers(HttpMethod.POST,   "/users/*/orders").hasAnyAuthority(Role.MEMBER.name(), Role.ADMIN.name())
                .antMatchers(HttpMethod.POST,   "/users/*/orders/*").hasAnyAuthority(Role.MEMBER.name(), Role.ADMIN.name())
                .anyRequest().authenticated()
                .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .logout()
                .logoutUrl("/users/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler(
                        ((request, response, authentication) ->
                                SecurityContextHolder.clearContext()
                        )
                );

        return http.build();
    }


}
