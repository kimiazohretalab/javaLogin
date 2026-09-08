package org.example.javalogin.servlet;

import com.google.gson.Gson;
import org.example.javalogin.config.JPAUtil;
import org.example.javalogin.dto.RegisterRequest;
import org.example.javalogin.service.UserService;
import org.example.javalogin.util.ValidationUtil;

import javax.persistence.EntityManager;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@WebServlet("/api/register")
public class RegisterServlet extends HttpServlet {

    private Gson gson;

    @Override
    public void init() {
        gson = new Gson();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        RegisterRequest registerRequest =
                gson.fromJson(
                        request.getReader(),
                        RegisterRequest.class
                );

        Set<ConstraintViolation<RegisterRequest>> violations =
                ValidationUtil.validate(registerRequest);

        if (!violations.isEmpty()) {

            Map<String, Object> result =
                    new HashMap<>();

            result.put("success", false);
            result.put("message", "Validation failed");

            Map<String, String> errors =
                    new HashMap<>();

            for (ConstraintViolation<RegisterRequest> violation
                    : violations) {

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
            entityManager.getTransaction().begin();
            UserService userService =
                    new UserService(entityManager);

            boolean registered =
                    userService.register(
                            registerRequest.getUsername(),
                            registerRequest.getPassword()
                    );

            if (registered) {

                entityManager.getTransaction().commit();

                Map<String, Object> result =
                        new HashMap<>();

                result.put("success", true);
                result.put(
                        "message",
                        "Registration successful"
                );

                response.setStatus(
                        HttpServletResponse.SC_CREATED
                );

                response.getWriter().write(
                        gson.toJson(result)
                );

            } else {

                entityManager.getTransaction().rollback();

                Map<String, Object> result =
                        new HashMap<>();

                result.put("success", false);
                result.put(
                        "message",
                        "Username already exists"
                );

                response.setStatus(
                        HttpServletResponse.SC_CONFLICT
                );

                response.getWriter().write(
                        gson.toJson(result)
                );
            }

        } finally {
            entityManager.close();
        }
    }
}