package org.example.javalogin.filter;

import org.example.javalogin.util.JwtUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/api/*")
public class JwtAuthenticationFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();


        if (path.endsWith("/api/login")
                || path.endsWith("/api/register")) {

            chain.doFilter(request, response);
            return;
        }

        String authorization =
                httpRequest.getHeader("Authorization");


        if (authorization == null
                || !authorization.startsWith("Bearer ")) {

            sendUnauthorized(httpResponse);
            return;
        }

        String token = authorization.substring(7);

        if (!JwtUtil.isValid(token)) {
            sendUnauthorized(httpResponse);
            return;
        }

        String username =
                JwtUtil.getUsername(token);

        httpRequest.setAttribute("username", username);

        chain.doFilter(request, response);
    }

    private void sendUnauthorized(
            HttpServletResponse response
    ) throws IOException {

        response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
        );

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                "{\"success\":false,\"message\":\"Unauthorized\"}"
        );
    }
}