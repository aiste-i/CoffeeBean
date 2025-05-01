package org.coffee.web.filter;

import org.coffee.persistence.entity.enums.EmployeeRole;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

// Apply this filter ONLY to the paths you want to protect
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/admin/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
        System.out.println("AuthenticationFilter Initialized for /admin/*");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false); // Get session only if it exists

        boolean loggedIn = false;
        boolean authorized = false;

        // --- Check if user is logged in based on session attributes YOU set ---
        if (session != null) {
            Object loggedInUserType = session.getAttribute("loggedInUserType");
            Object loggedInUserRole = session.getAttribute("loggedInUserRole"); // Retrieve role

            if ("Employee".equals(loggedInUserType)) { // Check if it's an Employee session
                loggedIn = true;
                // Check if the role is sufficient (ADMIN or EMPLOYEE)
                if (loggedInUserRole == EmployeeRole.ADMIN || loggedInUserRole == EmployeeRole.EMPLOYEE) {
                    authorized = true;
                }
            }
        }
        // ----------------------------------------------------------------------


        String loginURI = httpRequest.getContextPath() + "/admin/login.xhtml";
        String requestedURI = httpRequest.getRequestURI();

        boolean loginRequest = requestedURI.equals(loginURI);
        // Also allow access to resources needed by the login page (CSS, JS)
        boolean resourceRequest = requestedURI.contains("/javax.faces.resource/");


        if (loggedIn && authorized) {
            // User is logged in and has the correct role, allow access
            System.out.println("AuthFilter: User logged in and authorized. Proceeding.");
            chain.doFilter(request, response); // Continue to the requested resource
        } else if (loginRequest || resourceRequest) {
            // Allow access to the login page itself and its resources
            System.out.println("AuthFilter: Allowing access to login page or resource.");
            chain.doFilter(request, response);
        }
        else if (loggedIn && !authorized) {
            // User is logged in but doesn't have the right role
            System.out.println("AuthFilter: User logged in but NOT authorized. Redirecting to access denied (or login).");
            // You might want a dedicated access denied page
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            // Or redirect back to login
            // httpResponse.sendRedirect(loginURI);
        }
        else {
            // User is not logged in, redirect to the login page
            System.out.println("AuthFilter: User not logged in. Redirecting to login page.");
            // Store the original requested URL so login can redirect back (Optional)
            // You could store requestedURI in the session before redirecting
            httpResponse.sendRedirect(loginURI);
        }
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
