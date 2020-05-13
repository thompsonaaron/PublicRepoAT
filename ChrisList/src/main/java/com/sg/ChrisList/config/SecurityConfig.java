/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.ChrisList.config;

import com.sg.ChrisList.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 * @author board
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    UserService service;

//    @Autowired
//    public void configureGlobalInMemory(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("cference").password("{noop}password").roles("USER")
//                .and()
//                .withUser("admin").password("{noop}password").roles("ADMIN", "USER");
//    }
//    
    
    @Autowired
    public void configureGlobalInDb(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(service).passwordEncoder(bCryptPasswordEncoder());
    }
    
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/admin").hasRole("ADMIN")
            .antMatchers("/account", "/createlisting", "/addlisting").hasRole("USER")
            .antMatchers("/", "/home", "/createAccount", "/search", "/listings", "/viewListing", "/founders", "/filterByCondition", "/sortByPrice").permitAll()
            .antMatchers("/css/**", "/js/**", "/fonts/**", "/images/**").permitAll()
            .anyRequest().hasRole("USER")
                
        .and()
                
        .formLogin()
            .loginPage("/login")
            .failureUrl("/login?login_error=1")
            .permitAll()
                
        .and()
                
        .logout()
            .logoutSuccessUrl("/")
            .permitAll();
    }
}
