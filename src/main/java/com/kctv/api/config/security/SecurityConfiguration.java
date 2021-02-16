package com.kctv.api.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

// import 생략


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {



    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfiguration(@Lazy JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {

        return super.authenticationManagerBean();
    }


    /*
    * Access-Contorl-Allow-Origin
    * CORS 요청을 허용할 사이트 (e.g. https://oddpoet.net)
    *
    * Access-Contorl-Allow-Method
    * CORS 요청을 허용할 Http Method들 (e.g. GET,PUT,POST)
    *
    * Access-Contorl-Allow-Headers
    * 특정 헤더를 가진 경우에만 CORS 요청을 허용할 경우
    *
    * Access-Contorl-Allow-Credencial
    * 자격증명과 함께 요청을 할 수 있는지 여부.
    * 해당 서버에서 Authorization로 사용자 인증도 서비스할 것이라면 true로 응답해야 할 것이다.
    *
    * Access-Contorl-Max-Age
    * preflight 요청의 캐시 시간.
    * */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .httpBasic().disable() // rest api 이므로 기본설정 사용안함. 기본설정은 비인증시 로그인폼 화면으로 리다이렉트 된다.
                .csrf().disable() // rest api이므로 csrf 보안이 필요없으므로 disable처리.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt token으로 인증하므로 세션은 필요없으므로 생성안함.
                .and()
                .authorizeRequests() // 다음 리퀘스트에 대한 사용권한 체크
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll() //  현재 웹페이지 이외의 사이트(프론트와 서버가 분리되어 있을때)에 xhr 요청할 때 CORS preflight 라는 요청을 보낸다. 이 것은 실제 해당 서버에 CORS 정책을 확인하기 위한 요청이므로 허용한다.
                //.antMatchers("/v1/**").permitAll() // v1은 토큰체크 x (임시)
                .antMatchers("/image/**").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/ad/**").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/v1/find/password/**").permitAll() //이메일찾기
                .antMatchers("/v1/clk/**").permitAll() //클릭로그
                .antMatchers("/v1/store/**").permitAll() //상점정보
                .antMatchers("/v1/login").permitAll() //로그인
                .antMatchers("/v1/signup").permitAll() // 회원가입
                .antMatchers("/v1/check/**").permitAll() // 이메일 인증검사
                .antMatchers("/v1/place/**").permitAll() // 상점정보
                .antMatchers("/v1/places").permitAll() // 상점정보
                .antMatchers("/v1/tags/**").permitAll() // 태그 조회
                .antMatchers("/v1/verify/**").permitAll() // 태그 조회
                .antMatchers("/v1/card/**").permitAll() // 태그 조회
                .antMatchers("/v1/user/**").permitAll() // 태그 조회
                .antMatchers("/test/**").permitAll() // 태그 조회
                .antMatchers("/v1/search/**").permitAll() // 태그 조회
                .antMatchers("/v1/faq/**").permitAll() // 태그 조회
                .antMatchers("/v1/faq").permitAll() // 태그 조회
                .antMatchers("/v1/payment/**").permitAll() // 태그 조회

                .antMatchers("/exception/**").permitAll() // 토큰 예외처리
                .antMatchers(HttpMethod.GET, "helloworld/**").permitAll() // hellowworld로 시작하는 GET요청 리소스는 누구나 접근가능
                // 위 URL들은 토큰없이 접속 가능
                .antMatchers("/v1/admin/**").hasAnyRole("ADMIN","AP_ADMIN","CUSTOMER_ADMIN","WIRELESS_ADMIN")
                .anyRequest().hasRole("USER") // 그외 나머지 요청은 모두 인증된 회원만 접근 가능
                .and()
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint()) //authenticationEntryPoint로 필터단에서 나는 예외 처리
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class); // jwt token 필터를 id/password 인증 필터 전에 넣는다

    }

    @Override // ignore check swagger resource
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**",
                "/swagger-ui.html", "/webjars/**", "/swagger/**","/swagger-ui/**");

    }
}