package com.codingshuttle.linkedin.posts_service.config;

import com.codingshuttle.linkedin.posts_service.interceptor.RequestIdInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RequestIdInterceptor requestIdInterceptor;

    @Override
    public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
        registry.addInterceptor(requestIdInterceptor)
                .addPathPatterns("/**");
    }
}
