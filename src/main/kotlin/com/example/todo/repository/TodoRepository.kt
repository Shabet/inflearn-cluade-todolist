package com.example.todo.repository

import com.example.todo.entity.Todo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository : JpaRepository<Todo, Long> {
    // 완료 상태별 조회
    fun findByIsDone(isDone: Boolean): List<Todo>
    
    // 제목으로 검색
    fun findByTitleContainingIgnoreCase(title: String): List<Todo>
}
