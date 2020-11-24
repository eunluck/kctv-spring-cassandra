package com.kctv.api.config;


import com.fasterxml.classmate.TypeResolver;

import com.kctv.api.model.swagger.LoginEx;
import com.kctv.api.model.swagger.SignUpEx;
import com.kctv.api.model.swagger.UserUpdateEx;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    @Bean
    public Docket swaggerApi(TypeResolver resolver) {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(swaggerInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("com.kctv.api.controller")) //해당 패키지의 컨트롤러 내용을 읽어 맵핑된 리소스들을 문서화 시킴
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false)// 기본으로 세팅되는 200,401,403,404 메시지를 표시 하지 않음
                .additionalModels(resolver.resolve(UserUpdateEx.class)).additionalModels(resolver.resolve(SignUpEx.class)).additionalModels(resolver.resolve(LoginEx.class)); // request parameter Eample을 정의한 클래스 파일 로딩
    }

    private ApiInfo swaggerInfo() {
        return new ApiInfoBuilder().title("Spring - Cassandra KCTV API Documentation")
                .description("kctv API 문서입니다")
                .license("biskit")
                .licenseUrl("http://biskitlab.com")
                .version("1").build();
    }
}