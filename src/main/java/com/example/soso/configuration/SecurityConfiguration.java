package com.example.soso.configuration;

import com.example.soso.jwt.AccessDeniedHandlerException;
import com.example.soso.jwt.AuthenticationEntryPointException;
import com.example.soso.jwt.TokenProvider;
<<<<<<< HEAD
=======
import com.example.soso.service.UserDetailsServiceImpl;
>>>>>>> 662a55560bc07d664388a66946b308995fba5354
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
<<<<<<< HEAD
=======
import org.springframework.http.HttpMethod;
>>>>>>> 662a55560bc07d664388a66946b308995fba5354
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfiguration {

    @Value("${jwt.secret}")
    String SECRET_KEY;
    private final TokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationEntryPointException authenticationEntryPointException;
    private final AccessDeniedHandlerException accessDeniedHandlerException;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors();

        http.csrf().disable()

<<<<<<< HEAD
=======
                .headers().frameOptions().disable()
                .and()
>>>>>>> 662a55560bc07d664388a66946b308995fba5354
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPointException)
                .accessDeniedHandler(accessDeniedHandlerException)

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/api/member/**").permitAll()
                .antMatchers("/api/post/**").permitAll()
<<<<<<< HEAD
                .antMatchers("/api/comment/**").permitAll()
                .antMatchers("/api/subComment/**").permitAll()
                .antMatchers("/api/auth/image").permitAll()
=======
                .antMatchers("/api/auth/post/**").permitAll()
                .antMatchers("/api/comment/**").permitAll()
                .antMatchers("/api/subComment/**").permitAll()
//                .antMatchers("/api/auth/image").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**/*").permitAll()
                .antMatchers("http://localhost:3000").permitAll()
                .antMatchers("http://localhost:3001").permitAll()
//                .antMatchers("/api/member/**").permitAll()


>>>>>>> 662a55560bc07d664388a66946b308995fba5354
                .anyRequest().authenticated()

                .and()
                .apply(new JwtSecurityConfiguration(SECRET_KEY, tokenProvider, userDetailsService));

        return http.build();
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 662a55560bc07d664388a66946b308995fba5354
