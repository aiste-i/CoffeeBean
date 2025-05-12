package org.coffee.web.filter;

import org.coffee.persistence.entity.enums.EmployeeRole;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/admin/*"})
public class AuthenticationFilter implements Filter {

    private static final Set<String> ADMIN_ONLY_PATHS = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) {
        ADMIN_ONLY_PATHS.add("/admin/add-employee.xhtml");
        ADMIN_ONLY_PATHS.add("/admin/change-password.xhtml");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        EmployeeRole userRole = null;
        boolean loggedIn = false;

        if (session != null) {
            Object loggedInUsernameObj = session.getAttribute("loggedInUsername");
            Object loggedInUserRoleObj = session.getAttribute("loggedInUserRole");

            if (loggedInUsernameObj != null && loggedInUserRoleObj != null &&
                    !loggedInUsernameObj.toString().isEmpty() && loggedInUserRoleObj instanceof EmployeeRole) {

                loggedIn = true;
                userRole = (EmployeeRole) loggedInUserRoleObj;
            }
        }

        String contextPath = httpRequest.getContextPath();
        String loginURI = contextPath + "/admin/login.xhtml";
        String requestedURI = httpRequest.getRequestURI();
        String pathWithinContext = requestedURI.substring(contextPath.length());

        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);

        boolean loginRequest = requestedURI.equals(loginURI);
        boolean resourceRequest = requestedURI.contains("/javax.faces.resource/");

        if (loginRequest || resourceRequest) {
            chain.doFilter(request, response);
            return;
        }

        if (!loggedIn) {
            httpResponse.sendRedirect(loginURI);
            return;
        }

        if (ADMIN_ONLY_PATHS.contains(pathWithinContext)) {
            if (userRole == EmployeeRole.ADMIN) {
                chain.doFilter(request, response);
            } else {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            }
        }
        else if (userRole == EmployeeRole.ADMIN || userRole == EmployeeRole.EMPLOYEE) {
            chain.doFilter(request, response);
        }
        else {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        }
    }
}
