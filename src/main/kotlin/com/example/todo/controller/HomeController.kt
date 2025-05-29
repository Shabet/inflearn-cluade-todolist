package com.example.todo.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {

    /**
     * 홈페이지 리다이렉트
     * GET /
     */
    @GetMapping("/")
    fun home(): String {
        return "redirect:/todos"
    }
}
