package com.rz.bookapi.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// CORS filter to allow interaction between localhost API and localhost client
public class CorsFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
        // No initialization needed
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Set usual headers
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Total-Pages");

        // allow custom header
        response.setHeader("Access-Control-Expose-Headers", "X-Total-Pages");

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }
}