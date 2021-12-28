package com.lhstack.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author lhstack
 * @date 2021/12/11
 * @class SecurityConfiguration
 * @since 1.8
 */
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${SECURITY_USERNAME:admin}")
    private String username;

    @Value("${SECURITY_PASSWORD:123456}")
    private String password;

    @Value("${SECURITY_ENABLE:true}")
    private Boolean enable;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .passwordEncoder(passwordEncoder)
                .withUser(this.username)
                .password(passwordEncoder.encode(this.password))
                .roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (this.enable) {
            http.authorizeRequests()
                    .antMatchers("/actuator/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated();
        } else {
            http.authorizeRequests()
                    .anyRequest()
                    .permitAll();
        }
        http.csrf()
                .disable()
                .logout()
                .and()
                .httpBasic()
                .and()
                .formLogin()
                .disable();
    }
}
