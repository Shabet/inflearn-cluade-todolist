package com.example.todo.dto

import com.example.todo.entity.Todo
import jakarta.validation.constraints.NotBlank

data class TodoCreateRequestDto(
    @field:NotBlank(message = "제목은 필수입니다.")
    val title: String,
    val description: String? = null
) {
    fun toEntity(): Todo {
        return Todo(
            title = title.trim(),
            description = description?.trim(),
            isDone = false
        )
    }
}

data class TodoUpdateRequestDto(
    val title: String? = null,
    val description: String? = null,
    val isDone: Boolean? = null
)
