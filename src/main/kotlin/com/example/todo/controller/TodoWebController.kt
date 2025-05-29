package com.example.todo.controller

import com.example.todo.dto.TodoCreateRequestDto
import com.example.todo.dto.TodoUpdateRequestDto
import com.example.todo.service.TodoService
import com.example.user.dto.UserResponseDto
import com.example.user.service.UserService
import jakarta.servlet.http.HttpSession
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/todos")
class TodoWebController(
    private val todoService: TodoService,
    private val userService: UserService
) {

    /**
     * 현재 로그인한 사용자 정보 가져오기
     */
    private fun getCurrentUser(session: HttpSession): UserResponseDto {
        return session.getAttribute("loginUser") as? UserResponseDto
            ?: throw IllegalStateException("로그인이 필요합니다.")
    }

    /**
     * Todo 목록 페이지
     * GET /todos
     */
    @GetMapping
    fun listTodos(
        @RequestParam(required = false) isDone: Boolean?,
        @RequestParam(required = false) search: String?,
        session: HttpSession,
        model: Model
    ): String {
        val currentUser = getCurrentUser(session)
        val user = userService.getUserByEmail(currentUser.email)
            ?: throw IllegalStateException("사용자 정보를 찾을 수 없습니다.")

        val todoList = when {
            isDone != null -> todoService.getTodosByStatus(isDone, user)
            !search.isNullOrBlank() -> todoService.searchTodosByTitle(search, user)
            else -> todoService.getAllTodos(user)
        }
        
        model.addAttribute("todoList", todoList.todos)
        model.addAttribute("totalCount", todoList.totalCount)
        model.addAttribute("currentFilter", isDone)
        model.addAttribute("searchKeyword", search)
        model.addAttribute("currentUser", currentUser)
        
        return "todo/list"
    }

    /**
     * Todo 생성 폼 페이지
     * GET /todos/create
     */
    @GetMapping("/create")
    fun createForm(session: HttpSession, model: Model): String {
        val currentUser = getCurrentUser(session)
        model.addAttribute("todoCreateRequestDto", TodoCreateRequestDto("", ""))
        model.addAttribute("currentUser", currentUser)
        return "todo/create"
    }

    /**
     * Todo 생성 처리
     * POST /todos/create
     */
    @PostMapping("/create")
    fun createTodo(
        @Valid @ModelAttribute todoCreateRequestDto: TodoCreateRequestDto,
        bindingResult: BindingResult,
        session: HttpSession,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            return "todo/create"
        }

        try {
            val currentUser = getCurrentUser(session)
            val user = userService.getUserByEmail(currentUser.email)
                ?: throw IllegalStateException("사용자 정보를 찾을 수 없습니다.")

            todoService.createTodo(todoCreateRequestDto, user)
            redirectAttributes.addFlashAttribute("message", "할 일이 성공적으로 등록되었습니다.")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", "할 일 등록 중 오류가 발생했습니다.")
        }

        return "redirect:/todos"
    }

    /**
     * Todo 수정 폼 페이지
     * GET /todos/{id}/edit
     */
    @GetMapping("/{id}/edit")
    fun editForm(@PathVariable id: Long, session: HttpSession, model: Model): String {
        try {
            val currentUser = getCurrentUser(session)
            val user = userService.getUserByEmail(currentUser.email)
                ?: throw IllegalStateException("사용자 정보를 찾을 수 없습니다.")

            val todo = todoService.getTodoById(id, user)
            val updateRequestDto = TodoUpdateRequestDto(
                title = todo.title,
                description = todo.description,
                isDone = todo.isDone
            )
            model.addAttribute("todo", todo)
            model.addAttribute("todoUpdateRequestDto", updateRequestDto)
            model.addAttribute("currentUser", currentUser)
            return "todo/edit"
        } catch (e: NoSuchElementException) {
            model.addAttribute("error", "해당 할 일을 찾을 수 없습니다.")
            return "redirect:/todos"
        }
    }

    /**
     * Todo 수정 처리
     * POST /todos/{id}/edit
     */
    @PostMapping("/{id}/edit")
    fun updateTodo(
        @PathVariable id: Long,
        @Valid @ModelAttribute todoUpdateRequestDto: TodoUpdateRequestDto,
        bindingResult: BindingResult,
        session: HttpSession,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            try {
                val currentUser = getCurrentUser(session)
                val user = userService.getUserByEmail(currentUser.email)
                    ?: throw IllegalStateException("사용자 정보를 찾을 수 없습니다.")

                val todo = todoService.getTodoById(id, user)
                model.addAttribute("todo", todo)
                model.addAttribute("currentUser", currentUser)
                return "todo/edit"
            } catch (e: NoSuchElementException) {
                redirectAttributes.addFlashAttribute("error", "해당 할 일을 찾을 수 없습니다.")
                return "redirect:/todos"
            }
        }

        try {
            val currentUser = getCurrentUser(session)
            val user = userService.getUserByEmail(currentUser.email)
                ?: throw IllegalStateException("사용자 정보를 찾을 수 없습니다.")

            todoService.updateTodo(id, todoUpdateRequestDto, user)
            redirectAttributes.addFlashAttribute("message", "할 일이 성공적으로 수정되었습니다.")
        } catch (e: NoSuchElementException) {
            redirectAttributes.addFlashAttribute("error", "해당 할 일을 찾을 수 없습니다.")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", "수정 중 오류가 발생했습니다.")
        }

        return "redirect:/todos"
    }

    /**
     * Todo 완료 상태 토글
     * POST /todos/{id}/toggle
     */
    @PostMapping("/{id}/toggle")
    fun toggleTodoStatus(
        @PathVariable id: Long,
        session: HttpSession,
        redirectAttributes: RedirectAttributes
    ): String {
        try {
            val currentUser = getCurrentUser(session)
            val user = userService.getUserByEmail(currentUser.email)
                ?: throw IllegalStateException("사용자 정보를 찾을 수 없습니다.")

            val todo = todoService.getTodoById(id, user)
            val updateRequestDto = TodoUpdateRequestDto(isDone = !todo.isDone)
            todoService.updateTodo(id, updateRequestDto, user)
            
            val statusMessage = if (!todo.isDone) "완료 처리되었습니다." else "미완료 처리되었습니다."
            redirectAttributes.addFlashAttribute("message", "할 일이 $statusMessage")
        } catch (e: NoSuchElementException) {
            redirectAttributes.addFlashAttribute("error", "해당 할 일을 찾을 수 없습니다.")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", "상태 변경 중 오류가 발생했습니다.")
        }

        return "redirect:/todos"
    }

    /**
     * Todo 삭제
     * POST /todos/{id}/delete
     */
    @PostMapping("/{id}/delete")
    fun deleteTodo(
        @PathVariable id: Long,
        session: HttpSession,
        redirectAttributes: RedirectAttributes
    ): String {
        try {
            val currentUser = getCurrentUser(session)
            val user = userService.getUserByEmail(currentUser.email)
                ?: throw IllegalStateException("사용자 정보를 찾을 수 없습니다.")

            todoService.deleteTodo(id, user)
            redirectAttributes.addFlashAttribute("message", "할 일이 삭제되었습니다.")
        } catch (e: NoSuchElementException) {
            redirectAttributes.addFlashAttribute("error", "해당 할 일을 찾을 수 없습니다.")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", "삭제 중 오류가 발생했습니다.")
        }

        return "redirect:/todos"
    }
}
