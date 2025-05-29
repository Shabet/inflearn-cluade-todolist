package com.example.todo.repository

import com.example.todo.entity.Todo
import com.example.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository : JpaRepository<Todo, Long> {
    // 사용자별 Todo 조회
    fun findByUser(user: User): List<Todo>
    fun findByUserAndIsDone(user: User, isDone: Boolean): List<Todo>
    fun findByUserAndTitleContainingIgnoreCase(user: User, title: String): List<Todo>
    
    // 사용자와 ID로 Todo 조회
    fun findByIdAndUser(id: Long, user: User): Todo?
}
