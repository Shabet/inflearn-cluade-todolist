package com.example.todo.controller

import com.example.todo.dto.TodoCreateRequestDto
import com.example.todo.dto.TodoUpdateRequestDto
import com.example.todo.service.TodoService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/todos")
class TodoWebController(
    private val todoService: TodoService
) {

    /**
     * Todo 목록 페이지
     * GET /todos
     */
    @GetMapping
    fun listTodos(
        @RequestParam(required = false) isDone: Boolean?,
        @RequestParam(required = false) search: String?,
        model: Model
    ): String {
        val todoList = when {
            isDone != null -> todoService.getTodosByStatus(isDone)
            !search.isNullOrBlank() -> todoService.searchTodosByTitle(search)
            else -> todoService.getAllTodos()
        }
        
        model.addAttribute("todoList", todoList.todos)
        model.addAttribute("totalCount", todoList.totalCount)
        model.addAttribute("currentFilter", isDone)
        model.addAttribute("searchKeyword", search)
        
        return "todo/list"
    }

    /**
     * Todo 생성 폼 페이지
     * GET /todos/create
     */
    @GetMapping("/create")
    fun createForm(model: Model): String {
        model.addAttribute("todoCreateRequestDto", TodoCreateRequestDto("", ""))
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
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            return "todo/create"
        }

        try {
            todoService.createTodo(todoCreateRequestDto)
            redirectAttributes.addFlashAttribute("message", "할 일이 성공적으로 등록되었습니다.")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", "할 일 등록 중 오류가 발생했습니다.")
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
        redirectAttributes: RedirectAttributes
    ): String {
        try {
            val todo = todoService.getTodoById(id)
            val updateRequestDto = TodoUpdateRequestDto(isDone = !todo.isDone)
            todoService.updateTodo(id, updateRequestDto)
            
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
     * Todo 수정 폼 페이지
     * GET /todos/{id}/edit
     */
    @GetMapping("/{id}/edit")
    fun editForm(@PathVariable id: Long, model: Model): String {
        try {
            val todo = todoService.getTodoById(id)
            val updateRequestDto = TodoUpdateRequestDto(
                title = todo.title,
                description = todo.description,
                isDone = todo.isDone
            )
            model.addAttribute("todo", todo)
            model.addAttribute("todoUpdateRequestDto", updateRequestDto)
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
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            try {
                val todo = todoService.getTodoById(id)
                model.addAttribute("todo", todo)
                return "todo/edit"
            } catch (e: NoSuchElementException) {
                redirectAttributes.addFlashAttribute("error", "해당 할 일을 찾을 수 없습니다.")
                return "redirect:/todos"
            }
        }

        try {
            todoService.updateTodo(id, todoUpdateRequestDto)
            redirectAttributes.addFlashAttribute("message", "할 일이 성공적으로 수정되었습니다.")
        } catch (e: NoSuchElementException) {
            redirectAttributes.addFlashAttribute("error", "해당 할 일을 찾을 수 없습니다.")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", "수정 중 오류가 발생했습니다.")
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
        redirectAttributes: RedirectAttributes
    ): String {
        try {
            todoService.deleteTodo(id)
            redirectAttributes.addFlashAttribute("message", "할 일이 삭제되었습니다.")
        } catch (e: NoSuchElementException) {
            redirectAttributes.addFlashAttribute("error", "해당 할 일을 찾을 수 없습니다.")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", "삭제 중 오류가 발생했습니다.")
        }

        return "redirect:/todos"
    }


}
