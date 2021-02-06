package com.kctv.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


// CROS 오류 해제
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    public WebConfiguration() {}

}


