package com.example.onederful.filter;

import com.example.onederful.security.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter implements Filter {

    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(
        ServletRequest servletRequest,
        ServletResponse servletResponse,
        FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();

        String authorizationHeader = request.getHeader("Authorization");

        if("OPTIONS".equals(request.getMethod())) {
            filterChain.doFilter(servletRequest, servletResponse);
        }

        // 회원가입, 로그인 경우
        if (requestURI.startsWith("/api/auth/register") || requestURI.startsWith("/api/auth/login")
            ||
            requestURI.startsWith("/swagger-ui") ||
            requestURI.startsWith("/v3/api-docs") ||
            requestURI.startsWith("/swagger-resources") ||
            requestURI.startsWith("/api/test") ||
            requestURI.startsWith("/webjars")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 토큰 존재 유무 확인
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            errorResponse(response,HttpServletResponse.SC_UNAUTHORIZED,"인증이 필요합니다");
            return;
        }

        // "Bearer" 빼고 확인
        String jwt = authorizationHeader.substring(7);

        // 토큰 검증
        String errorMessage = jwtUtil.validateToken(jwt);
        if (errorMessage != null) {
            errorResponse(response, HttpServletResponse.SC_FORBIDDEN, errorMessage);
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    // 공통 에러 응답 처리
    private void errorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=utf-8");

        String json = "{" +
                "\"success\" : false," +
                "\"message\": \""+ message + "\"," +
                "\"data\" : null," +
                "\"timestamp\" : \"" + OffsetDateTime.now() + "\"" +
                "}";

        response.getWriter().write(json);
    }

}