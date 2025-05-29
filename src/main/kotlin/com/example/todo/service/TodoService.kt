package com.example.todo.service

import com.example.todo.dto.*
import com.example.todo.entity.Todo
import com.example.todo.repository.TodoRepository
import com.example.user.entity.User
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
    fun createTodo(requestDto: TodoCreateRequestDto, user: User): TodoResponseDto {
        val todo = requestDto.toEntity(user)
        val savedTodo = todoRepository.save(todo)
        return TodoResponseDto.from(savedTodo)
    }

    /**
     * 사용자의 모든 Todo 조회
     */
    fun getAllTodos(user: User): TodoListResponseDto {
        val todos = todoRepository.findByUser(user)
        return TodoListResponseDto.from(todos)
    }

    /**
     * ID로 사용자의 Todo 단건 조회
     */
    fun getTodoById(id: Long, user: User): TodoResponseDto {
        val todo = todoRepository.findByIdAndUser(id, user)
            ?: throw NoSuchElementException("ID가 $id 인 Todo를 찾을 수 없습니다.")
        return TodoResponseDto.from(todo)
    }

    /**
     * 완료 상태별 사용자의 Todo 조회
     */
    fun getTodosByStatus(isDone: Boolean, user: User): TodoListResponseDto {
        val todos = todoRepository.findByUserAndIsDone(user, isDone)
        return TodoListResponseDto.from(todos)
    }

    /**
     * 제목으로 사용자의 Todo 검색
     */
    fun searchTodosByTitle(title: String, user: User): TodoListResponseDto {
        val todos = todoRepository.findByUserAndTitleContainingIgnoreCase(user, title)
        return TodoListResponseDto.from(todos)
    }

    /**
     * 사용자의 Todo 수정
     */
    @Transactional
    fun updateTodo(id: Long, requestDto: TodoUpdateRequestDto, user: User): TodoResponseDto {
        val todo = todoRepository.findByIdAndUser(id, user)
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
     * 사용자의 Todo 삭제
     */
    @Transactional
    fun deleteTodo(id: Long, user: User) {
        val todo = todoRepository.findByIdAndUser(id, user)
            ?: throw NoSuchElementException("ID가 $id 인 Todo를 찾을 수 없습니다.")
        
        todoRepository.delete(todo)
    }

    /**
     * 사용자의 모든 Todo 삭제
     */
    @Transactional
    fun deleteAllTodos(user: User) {
        val todos = todoRepository.findByUser(user)
        todoRepository.deleteAll(todos)
    }
}
