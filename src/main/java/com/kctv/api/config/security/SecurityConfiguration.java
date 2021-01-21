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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token","Authorization"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
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
                //.antMatchers("/v1/**").permitAll() // v1은 토큰체크 x (임시)
                .antMatchers("/image/**").permitAll()
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

                .antMatchers("/exception/**").permitAll() // 토큰 예외처리
                .antMatchers(HttpMethod.GET, "helloworld/**").permitAll() // hellowworld로 시작하는 GET요청 리소스는 누구나 접근가능
                // 위 URL들은 토큰없이 접속 가능
                .antMatchers("/v1/admin/**").hasRole("ADMIN")
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