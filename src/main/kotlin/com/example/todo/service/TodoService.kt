package com.example.todo.service

import com.example.todo.dto.*
import com.example.todo.entity.Todo
import com.example.todo.repository.TodoRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TodoService(
    private val todoRepository: TodoRepository
) {

    /**
     * 새로운 Todo 생성
     */
    @Transactional
    fun createTodo(requestDto: TodoCreateRequestDto): TodoResponseDto {
        val todo = requestDto.toEntity()
        val savedTodo = todoRepository.save(todo)
        return TodoResponseDto.from(savedTodo)
    }

    /**
     * 모든 Todo 조회
     */
    fun getAllTodos(): TodoListResponseDto {
        val todos = todoRepository.findAll()
        return TodoListResponseDto.from(todos)
    }

    /**
     * ID로 Todo 단건 조회
     */
    fun getTodoById(id: Long): TodoResponseDto {
        val todo = todoRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException("ID가 $id 인 Todo를 찾을 수 없습니다.")
        return TodoResponseDto.from(todo)
    }

    /**
     * 완료 상태별 Todo 조회
     */
    fun getTodosByStatus(isDone: Boolean): TodoListResponseDto {
        val todos = todoRepository.findByIsDone(isDone)
        return TodoListResponseDto.from(todos)
    }

    /**
     * 제목으로 Todo 검색
     */
    fun searchTodosByTitle(title: String): TodoListResponseDto {
        val todos = todoRepository.findByTitleContainingIgnoreCase(title)
        return TodoListResponseDto.from(todos)
    }

    /**
     * Todo 수정
     */
    @Transactional
    fun updateTodo(id: Long, requestDto: TodoUpdateRequestDto): TodoResponseDto {
        val todo = todoRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException("ID가 $id 인 Todo를 찾을 수 없습니다.")

        // 요청된 필드만 업데이트 (null이 아닌 경우에만)
        requestDto.title?.let { 
            if (it.isNotBlank()) {
                todo.title = it.trim() 
            }
        }
        requestDto.description?.let { todo.description = it.trim().ifEmpty { null } }
        requestDto.isDone?.let { todo.isDone = it }

        val updatedTodo = todoRepository.save(todo)
        return TodoResponseDto.from(updatedTodo)
    }

    /**
     * Todo 삭제
     */
    @Transactional
    fun deleteTodo(id: Long) {
        val todo = todoRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException("ID가 $id 인 Todo를 찾을 수 없습니다.")
        
        todoRepository.delete(todo)
    }

    /**
     * 모든 Todo 삭제
     */
    @Transactional
    fun deleteAllTodos() {
        todoRepository.deleteAll()
    }
}
