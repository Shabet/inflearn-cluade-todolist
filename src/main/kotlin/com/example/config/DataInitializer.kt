package com.example.config

import com.example.todo.entity.Todo
import com.example.todo.repository.TodoRepository
import com.example.user.entity.User
import com.example.user.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val todoRepository: TodoRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        // 테스트 사용자가 없으면 생성
        if (!userRepository.existsByEmail("test@example.com")) {
            val testUser = User(
                email = "test@example.com",
                password = passwordEncoder.encode("123456"),
                nickname = "테스트유저"
            )
            val savedUser = userRepository.save(testUser)

            // 테스트 Todo 데이터 생성
            val todos = listOf(
                Todo(
                    title = "Spring Boot 학습하기",
                    description = "Kotlin과 Spring Boot를 활용한 웹 개발 학습",
                    isDone = false,
                    user = savedUser
                ),
                Todo(
                    title = "JPA 공부하기",
                    description = "JPA와 Hibernate 학습",
                    isDone = true,
                    user = savedUser
                ),
                Todo(
                    title = "Thymeleaf 템플릿 엔진 익히기",
                    description = "Thymeleaf를 사용한 동적 웹 페이지 작성",
                    isDone = false,
                    user = savedUser
                )
            )

            todoRepository.saveAll(todos)
            println("테스트 데이터가 초기화되었습니다.")
            println("테스트 계정: test@example.com / 123456")
        }
    }
}
