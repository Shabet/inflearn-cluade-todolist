package com.example.todo.dto

import com.example.todo.entity.Todo
import com.example.user.entity.User
import jakarta.validation.constraints.NotBlank

data class TodoCreateRequestDto(
    @field:NotBlank(message = "제목은 필수입니다.")
    val title: String,
    val description: String? = null
) {
    fun toEntity(user: User): Todo {
        return Todo(
            title = title.trim(),
            description = description?.trim(),
            isDone = false,
            user = user
        )
    }
}

data class TodoUpdateRequestDto(
    @field:NotBlank(message = "제목은 필수입니다.")
    val title: String? = null,
    val description: String? = null,
    val isDone: Boolean? = null
)
