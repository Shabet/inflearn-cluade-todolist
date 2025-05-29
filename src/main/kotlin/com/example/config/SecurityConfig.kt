package com.example.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers("/login", "/signup", "/css/**", "/js/**", "/h2-console/**", "/error").permitAll()
                    .anyRequest().permitAll() // 인터셉터에서 인증 처리
            }
            .csrf { csrf ->
                csrf.disable() // H2 콘솔 접근 및 간편한 테스트를 위해 비활성화
            }
            .headers { headers ->
                headers.frameOptions().disable() // H2 콘솔을 위해
            }
            .formLogin { form ->
                form.disable() // 커스텀 로그인 폼 사용
            }
            .httpBasic { basic ->
                basic.disable()
            }

        return http.build()
    }
}
