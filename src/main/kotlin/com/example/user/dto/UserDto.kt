package com.example.user.dto

import com.example.user.entity.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserSignupRequestDto(
    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수입니다.")
    @field:Size(min = 6, max = 20, message = "비밀번호는 6자 이상 20자 이하여야 합니다.")
    val password: String,

    @field:NotBlank(message = "비밀번호 확인은 필수입니다.")
    val passwordConfirm: String,

    @field:NotBlank(message = "닉네임은 필수입니다.")
    @field:Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하여야 합니다.")
    val nickname: String
) {
    fun toEntity(encodedPassword: String): User {
        return User(
            email = email.trim(),
            password = encodedPassword,
            nickname = nickname.trim()
        )
    }
}

data class UserLoginRequestDto(
    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수입니다.")
    val password: String
)

data class UserResponseDto(
    val id: Long,
    val email: String,
    val nickname: String
) {
    companion object {
        fun from(user: User): UserResponseDto {
            return UserResponseDto(
                id = user.id,
                email = user.email,
                nickname = user.nickname
            )
        }
    }
}
