package com.lhstack.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * @author lhstack
 * @date 2021/12/11
 * @class SecurityConfiguration
 * @since 1.8
 */
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Value("${SECURITY_USERNAME:admin}")
    private String username;

    @Value("${SECURITY_PASSWORD:123456}")
    private String password;

    @Value("${SECURITY_ENABLE:true}")
    private Boolean enable;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http, PasswordEncoder passwordEncoder) {
        if (this.enable) {
            http.authorizeExchange()
                    .pathMatchers("/actuator/**")
                    .permitAll()
                    .anyExchange()
                    .authenticated();
        } else {
            http.authorizeExchange()
                    .anyExchange()
                    .permitAll();
        }
        http.csrf()
                .disable()
                .logout()
                .and()
                .httpBasic()
                .and()
                .formLogin()
                .authenticationManager(buildAuthenticationManager(passwordEncoder));
        return http.build();
    }

    private ReactiveAuthenticationManager buildAuthenticationManager(PasswordEncoder passwordEncoder) {
        MapReactiveUserDetailsService mapReactiveUserDetailsService = new MapReactiveUserDetailsService(
                User.withUsername(this.username)
                        .password(String.format("{bcrypt}%s", passwordEncoder.encode(this.password)))
                        .roles("ADMIN")
                        .build()
        );
        return new UserDetailsRepositoryReactiveAuthenticationManager(mapReactiveUserDetailsService);
    }
}
