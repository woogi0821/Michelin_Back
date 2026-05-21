package com.simplecoding.michelin_back.config;

import com.simplecoding.michelin_back.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ── 공개 허용 ───────────────────────────────
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/api-docs/**"
                        ).permitAll()
                        // 음식점 조회 (공개)
                        .requestMatchers(
                                "/api/restaurants",
                                "/api/restaurants/**",
                                "/restaurants/**" // ✅ 이 줄이 꼭 있어야 합니다!
                        ).permitAll()
                        // 💡 [추가] 팝업 광고 API 공개 허용
                        .requestMatchers(
                                "/api/v1/ads",
                                "/api/v1/ads/**"
                        ).permitAll()
                        // 이미지 (공개)
                        .requestMatchers("/images/**").permitAll()
                        // 로그인/회원가입 (공개)
                        .requestMatchers("/api/auth/**").permitAll()
                        // 리뷰 조회 GET만 공개 ✅ HttpMethod.GET 으로 수정
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                        // ── 인증 필요 ───────────────────────────────
                        .anyRequest().authenticated()
                )
                // ✅ JWT 필터 연결
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}