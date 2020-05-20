/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaronthompson.fartlek.config;

import com.aaronthompson.fartlek.service.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import software.amazon.awssdk.services.quicksight.model.MySqlParameters;

/**
 *
 * @author thomp
 */
@Configuration
@EnableWebSecurity(debug = false)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    ServiceImpl service;

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
                .antMatchers("/account").hasRole("USER")
                .antMatchers("/", "/home", "/allWorkouts", "/createAccount", "/trainingLog", "/addWorkout").permitAll()
                .antMatchers("/css/**", "/js/**", "/fonts/**").permitAll()
                .anyRequest().hasRole("USER")
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/home")
                .failureUrl("/login?login_error=1")
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .permitAll();
    }

    // configuration for DB in AWS
//    mySql client = MySqlParameters.Builder.database("fartlek");
//    
//    S3Client s3 = S3Client.builder()
//            .region(Region.US_WEST_2)
//            .build();

}
