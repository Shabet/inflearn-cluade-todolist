package com.example.user.service

import com.example.user.dto.UserLoginRequestDto
import com.example.user.dto.UserResponseDto
import com.example.user.dto.UserSignupRequestDto
import com.example.user.entity.User
import com.example.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    /**
     * 회원가입
     */
    @Transactional
    fun signup(requestDto: UserSignupRequestDto): UserResponseDto {
        // 비밀번호 확인 검증
        if (requestDto.password != requestDto.passwordConfirm) {
            throw IllegalArgumentException("비밀번호가 일치하지 않습니다.")
        }

        // 이메일 중복 확인
        if (userRepository.existsByEmail(requestDto.email)) {
            throw IllegalArgumentException("이미 사용 중인 이메일입니다.")
        }

        // 비밀번호 암호화
        val encodedPassword = passwordEncoder.encode(requestDto.password)
        
        // 사용자 저장
        val user = requestDto.toEntity(encodedPassword)
        val savedUser = userRepository.save(user)
        
        return UserResponseDto.from(savedUser)
    }

    /**
     * 로그인
     */
    fun login(requestDto: UserLoginRequestDto): UserResponseDto {
        val user = userRepository.findByEmail(requestDto.email)
            ?: throw IllegalArgumentException("존재하지 않는 이메일입니다.")

        if (!passwordEncoder.matches(requestDto.password, user.password)) {
            throw IllegalArgumentException("비밀번호가 올바르지 않습니다.")
        }

        return UserResponseDto.from(user)
    }

    /**
     * 사용자 조회
     */
    fun getUserById(id: Long): UserResponseDto {
        val user = userRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException("사용자를 찾을 수 없습니다.")
        return UserResponseDto.from(user)
    }

    /**
     * 이메일로 사용자 조회
     */
    fun getUserByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }
}
