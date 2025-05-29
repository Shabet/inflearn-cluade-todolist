package com.example.user.controller

import com.example.user.dto.UserLoginRequestDto
import com.example.user.dto.UserSignupRequestDto
import com.example.user.service.UserService
import jakarta.servlet.http.HttpSession
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class UserController(
    private val userService: UserService
) {

    /**
     * 회원가입 폼 페이지
     * GET /signup
     */
    @GetMapping("/signup")
    fun signupForm(model: Model): String {
        model.addAttribute("userSignupRequestDto", UserSignupRequestDto("", "", "", ""))
        return "user/signup"
    }

    /**
     * 회원가입 처리
     * POST /signup
     */
    @PostMapping("/signup")
    fun signup(
        @Valid @ModelAttribute userSignupRequestDto: UserSignupRequestDto,
        bindingResult: BindingResult,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            return "user/signup"
        }

        try {
            userService.signup(userSignupRequestDto)
            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.")
            return "redirect:/login"
        } catch (e: IllegalArgumentException) {
            model.addAttribute("error", e.localizedMessage)
            return "user/signup"
        } catch (e: Exception) {
            model.addAttribute("error", "회원가입 중 오류가 발생했습니다.")
            return "user/signup"
        }
    }

    /**
     * 로그인 폼 페이지
     * GET /login
     */
    @GetMapping("/login")
    fun loginForm(model: Model): String {
        model.addAttribute("userLoginRequestDto", UserLoginRequestDto("", ""))
        return "user/login"
    }

    /**
     * 로그인 처리
     * POST /login
     */
    @PostMapping("/login")
    fun login(
        @Valid @ModelAttribute userLoginRequestDto: UserLoginRequestDto,
        bindingResult: BindingResult,
        session: HttpSession,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            return "user/login"
        }

        try {
            val user = userService.login(userLoginRequestDto)
            session.setAttribute("loginUser", user)
            redirectAttributes.addFlashAttribute("message", "${user.nickname}님, 환영합니다!")
            return "redirect:/todos"
        } catch (e: IllegalArgumentException) {
            model.addAttribute("error", e.localizedMessage)
            return "user/login"
        } catch (e: Exception) {
            model.addAttribute("error", "로그인 중 오류가 발생했습니다.")
            return "user/login"
        }
    }

    /**
     * 로그아웃
     * POST /logout
     */
    @PostMapping("/logout")
    fun logout(session: HttpSession): String {
        session.invalidate()
        return "redirect:/login"
    }
}
