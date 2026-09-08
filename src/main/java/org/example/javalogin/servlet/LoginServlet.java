package org.example.javalogin.servlet;

import com.google.gson.Gson;
import org.example.javalogin.config.JPAUtil;
import org.example.javalogin.dto.LoginRequest;
import org.example.javalogin.service.UserService;
import org.example.javalogin.util.JwtUtil;
import org.example.javalogin.util.ValidationUtil;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    private Gson gson;

    @Override
    public void init() throws ServletException {
        gson = new Gson();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // خواندن JSON
        LoginRequest loginRequest =
                gson.fromJson(request.getReader(), LoginRequest.class);

        // Validation
        Set<ConstraintViolation<LoginRequest>> violations =
                ValidationUtil.validate(loginRequest);

        if (!violations.isEmpty()) {

            Map<String, Object> result = new HashMap<>();

            result.put("success", false);
            result.put("message", "Validation failed");

            Map<String, String> errors = new HashMap<>();

            for (ConstraintViolation<LoginRequest> violation : violations) {

                errors.put(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                );
            }

            result.put("errors", errors);

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().write(
                    gson.toJson(result)
            );

            return;
        }

        EntityManager entityManager =
                JPAUtil.createEntityManager();

        try {

            UserService userService =
                    new UserService(entityManager);

            boolean success = userService.login(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            Map<String, Object> result = new HashMap<>();

            if (success) {

                // ساخت JWT
                String token =
                        JwtUtil.generateToken(
                                loginRequest.getUsername()
                        );

                result.put("success", true);
                result.put("message", "Login successful");
                result.put("token", token);

                response.setStatus(
                        HttpServletResponse.SC_OK
                );

            } else {

                result.put("success", false);
                result.put(
                        "message",
                        "Invalid username or password"
                );

                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED
                );
            }

            response.getWriter().write(
                    gson.toJson(result)
            );

        } catch (Exception e) {

        e.printStackTrace();

        response.setStatus(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        );

        response.getWriter().write(
                "{\"success\":false,\"message\":\"Internal server error\"}"
        );

    } finally {

        entityManager.close();
    }
    }
}