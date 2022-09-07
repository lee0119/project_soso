package com.example.soso.configuration;

import com.example.soso.jwt.AccessDeniedHandlerException;
import com.example.soso.jwt.AuthenticationEntryPointException;
import com.example.soso.jwt.TokenProvider;
import com.example.soso.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/h2-console/**");
    }

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors();

        http.csrf().disable()
                .headers().frameOptions().disable()
                .and()
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
                .antMatchers("/api/comment/**").permitAll()
                .antMatchers("/api/subComment/**").permitAll()
                .antMatchers("/api/auth/post/**").permitAll()
                .antMatchers("/api/auth/comment/**").permitAll()
                .antMatchers("/api/auth/subComment/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**/*").permitAll()
                .anyRequest().authenticated()
                .and()
//                .apply(new JwtSecurityConfiguration(tokenProvider));
                .apply(new JwtSecurityConfiguration(tokenProvider));

        return http.build();
    }

    @Configuration
    public static class CorsConfig implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedHeaders("*")
                    .allowedMethods("*")
                    .maxAge(3000);

        }
    }

//    //Lucy Xss filter 적용
//    @Bean
//    public FilterRegistrationBean xssFilterBean() {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        registrationBean.setFilter(new XssEscapeServletFilter());
//        registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
//        registrationBean.addUrlPatterns("*.do", "*.jsp");
//        return registrationBean;
//    }
//
//    //requestBody xss 필터 적용(json/api)
//    @Bean
//    public MappingJackson2HttpMessageConverter jsonEscapeConverter() {
//        ObjectMapper copy = objectMapper.copy();
//        copy.getFactory().setCharacterEscapes(new HTMLCharacterEscapes());
//        return new MappingJackson2HttpMessageConverter(copy);
//    }
}
