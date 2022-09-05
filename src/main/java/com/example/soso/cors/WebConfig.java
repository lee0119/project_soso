//package com.example.soso.cors;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//@EnableWebMvc
//@RequiredArgsConstructor
//public class WebConfig implements WebMvcConfigurer {
//
//    private final ObjectMapper objectMapper;
//
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:3000")
//                .allowedMethods("*")
//                .allowCredentials(false)
//                .maxAge(3000)
//                .allowedHeaders("*")
//                .exposedHeaders("*");
//
//    }
//
//
//
//
//    //Lucy Xss filter 적용
//    @Bean
//    public FilterRegistrationBean xssFilterBean(){
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