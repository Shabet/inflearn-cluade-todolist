package com.example.todo.controller

import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {

    /**
     * 홈페이지 리다이렉트
     * GET /
     */
    @GetMapping("/")
    fun home(session: HttpSession): String {
        val loginUser = session.getAttribute("loginUser")
        return if (loginUser != null) {
            "redirect:/todos"
        } else {
            "redirect:/login"
        }
    }
}
