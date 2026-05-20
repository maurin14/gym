package com.una.ac.cr.gym.config;

import com.una.ac.cr.gym.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String path = request.getRequestURI();

        if (isProtectedPath(path)) {
            addNoCacheHeaders(response);

            HttpSession session = request.getSession(false);
            User user = session != null ? (User) session.getAttribute("user") : null;

            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }

            if (!hasAccess(path, user.getRole())) {
                response.sendRedirect(request.getContextPath() + defaultHome(user.getRole()));
                return false;
            }
        }

        return true;
    }

    private boolean isProtectedPath(String path) {
        return path.startsWith("/admin/")
                || path.startsWith("/trainer/")
                || path.startsWith("/client/")
                || path.startsWith("/user/")
                || path.startsWith("/users")
                || path.startsWith("/reports")
                || path.startsWith("/profile/")
                || path.startsWith("/payments")
                || path.startsWith("/branches")
                || path.startsWith("/classes")
                || path.startsWith("/attendances");
    }

    private boolean hasAccess(String path, String role) {
        if (path.startsWith("/admin/") || path.startsWith("/users") || path.startsWith("/reports")) {
            return "administrator".equals(role);
        }

        if (path.startsWith("/trainer/")) {
            return "trainer".equals(role) || "administrator".equals(role);
        }

        if (path.startsWith("/client/") || path.startsWith("/user/")
                || path.startsWith("/payments") || path.startsWith("/branches")) {
            return "client".equals(role);
        }

        if (path.startsWith("/classes") || path.startsWith("/attendances") || path.startsWith("/profile/")) {
            return "administrator".equals(role) || "trainer".equals(role) || "client".equals(role);
        }

        return true;
    }

    private String defaultHome(String role) {
        if ("administrator".equals(role)) {
            return "/admin/home";
        }
        if ("trainer".equals(role)) {
            return "/trainer/home";
        }
        return "/client/home";
    }

    private void addNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, private");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}
