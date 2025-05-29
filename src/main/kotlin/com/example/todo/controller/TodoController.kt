package com.example.todo.controller

import com.example.todo.dto.*
import com.example.todo.service.TodoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/todos")
class TodoController(
    private val todoService: TodoService
) {

    /**
     * Todo 생성
     * POST /api/todos
     */
    @PostMapping
    fun createTodo(@Valid @RequestBody requestDto: TodoCreateRequestDto): ResponseEntity<TodoResponseDto> {
        val todo = todoService.createTodo(requestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(todo)
    }

    /**
     * 모든 Todo 조회
     * GET /api/todos
     */
    @GetMapping
    fun getAllTodos(
        @RequestParam(required = false) isDone: Boolean?,
        @RequestParam(required = false) search: String?
    ): ResponseEntity<TodoListResponseDto> {
        val todos = when {
            isDone != null -> todoService.getTodosByStatus(isDone)
            !search.isNullOrBlank() -> todoService.searchTodosByTitle(search)
            else -> todoService.getAllTodos()
        }
        return ResponseEntity.ok(todos)
    }

    /**
     * Todo 단건 조회
     * GET /api/todos/{id}
     */
    @GetMapping("/{id}")
    fun getTodoById(@PathVariable id: Long): ResponseEntity<TodoResponseDto> {
        val todo = todoService.getTodoById(id)
        return ResponseEntity.ok(todo)
    }

    /**
     * Todo 수정
     * PUT /api/todos/{id}
     */
    @PutMapping("/{id}")
    fun updateTodo(
        @PathVariable id: Long,
        @RequestBody requestDto: TodoUpdateRequestDto
    ): ResponseEntity<TodoResponseDto> {
        val updatedTodo = todoService.updateTodo(id, requestDto)
        return ResponseEntity.ok(updatedTodo)
    }

    /**
     * Todo 삭제
     * DELETE /api/todos/{id}
     */
    @DeleteMapping("/{id}")
    fun deleteTodo(@PathVariable id: Long): ResponseEntity<Void> {
        todoService.deleteTodo(id)
        return ResponseEntity.noContent().build()
    }

    /**
     * 모든 Todo 삭제
     * DELETE /api/todos
     */
    @DeleteMapping
    fun deleteAllTodos(): ResponseEntity<Void> {
        todoService.deleteAllTodos()
        return ResponseEntity.noContent().build()
    }
}
