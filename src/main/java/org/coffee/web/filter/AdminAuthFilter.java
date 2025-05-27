package org.coffee.web.filter;

import org.coffee.persistence.entity.enums.UserRole;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.coffee.constants.Constants.SessionAttributeKeys.LOGGED_IN_USERNAME;
import static org.coffee.constants.Constants.SessionAttributeKeys.LOGGED_IN_USER_ROLE;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/admin/*"})
public class AdminAuthFilter implements Filter {

    private static final Set<String> FILTERED_PATHS = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) {
        FILTERED_PATHS.add("/admin/add-employee.xhtml");
        FILTERED_PATHS.add("/admin/change-password.xhtml");
        FILTERED_PATHS.add("/admin/dashboard.xhtml");
        FILTERED_PATHS.add("/admin/productManagement.xhtml");
        FILTERED_PATHS.add("/admin/categoryManagement.xhtml");
        FILTERED_PATHS.add("/admin/ingredientManagement.xhtml");
        FILTERED_PATHS.add("/admin/ingTypeManagement.xhtml");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        UserRole userRole = null;
        boolean loggedIn = false;

        if (session != null) {
            Object loggedInUsernameObj = session.getAttribute(LOGGED_IN_USERNAME );
            Object loggedInUserRoleObj = session.getAttribute(LOGGED_IN_USER_ROLE);

            if (loggedInUsernameObj != null && loggedInUserRoleObj != null &&
                    !loggedInUsernameObj.toString().isEmpty() && loggedInUserRoleObj instanceof UserRole) {

                loggedIn = true;
                userRole = (UserRole) loggedInUserRoleObj;
            }
        }

        String contextPath = httpRequest.getContextPath();
        String loginURI = contextPath + "/admin/login.xhtml";
        String dashboardURI = contextPath + "/admin/index.xhtml";
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

        if (FILTERED_PATHS.contains(pathWithinContext)) {
            if (userRole == UserRole.ADMIN) {
                chain.doFilter(request, response);
            } else {
                httpResponse.sendRedirect(dashboardURI);
            }
        }
        else if (userRole == UserRole.ADMIN || userRole == UserRole.EMPLOYEE) {
            chain.doFilter(request, response);
        }
        else {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        }
    }
}
