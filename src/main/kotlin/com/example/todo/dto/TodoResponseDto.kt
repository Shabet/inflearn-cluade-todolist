package com.example.todo.dto

import com.example.todo.entity.Todo
import java.time.LocalDateTime

data class TodoResponseDto(
    val id: Long,
    val title: String,
    val description: String?,
    val isDone: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(todo: Todo): TodoResponseDto {
            return TodoResponseDto(
                id = todo.id,
                title = todo.title,
                description = todo.description,
                isDone = todo.isDone,
                createdAt = todo.createdAt,
                updatedAt = todo.updatedAt
            )
        }
    }
}

data class TodoListResponseDto(
    val todos: List<TodoResponseDto>,
    val totalCount: Int
) {
    companion object {
        fun from(todos: List<Todo>): TodoListResponseDto {
            return TodoListResponseDto(
                todos = todos.map { TodoResponseDto.from(it) },
                totalCount = todos.size
            )
        }
    }
}
