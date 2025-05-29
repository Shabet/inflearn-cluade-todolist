package com.example.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AuthInterceptor : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val requestURI = request.requestURI
        
        // 로그인이 필요하지 않은 페이지들
        val allowedPaths = listOf(
            "/login", "/signup", "/css/", "/js/", "/h2-console/",
            "/error", "/favicon.ico"
        )
        
        // 허용된 경로인지 확인
        if (allowedPaths.any { requestURI.startsWith(it) }) {
            return true
        }
        
        // 세션에서 로그인 사용자 확인
        val session = request.session
        val loginUser = session.getAttribute("loginUser")
        
        if (loginUser == null) {
            // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
            response.sendRedirect("/login")
            return false
        }
        
        return true
    }
}
