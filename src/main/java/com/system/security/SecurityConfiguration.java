package com.system.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by mpereyma on 10/16/15.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
            .withUser("user1")
            .password("upassword1")
            .roles("USER")
            .and()
            .withUser("user2")
            .password("upassword2")
            .roles("USER")
            .and()
            .withUser("user3")
            .password("upassword3")
            .roles("USER")
            .and()
            .withUser("admin")
            .password("apassword")
            .roles("ADMIN", "USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable().httpBasic().and()
            .authorizeRequests()
            .antMatchers("/", "/restaurant", "/menu").hasRole("ADMIN")
            .antMatchers("/vote").hasRole("USER")
            .anyRequest().authenticated();
        http.headers().frameOptions().disable();

    }


}
