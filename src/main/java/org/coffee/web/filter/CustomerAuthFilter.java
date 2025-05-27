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

import static org.coffee.constants.Constants.SessionAttributeKeys.*;

@WebFilter(filterName = "CustomerAuthenticationFilter", urlPatterns = {"/user/*"})
public class CustomerAuthFilter implements Filter {

    private static final Set<String> FILTERED_PATHS = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) {
        FILTERED_PATHS.add("/user/email-change.xhtml");
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
            Object loggedInUsernameObj = session.getAttribute(LOGGED_IN_USER_EMAIL);
            Object loggedInUserRoleObj = session.getAttribute(LOGGED_IN_USER_ROLE);

            if (loggedInUsernameObj != null && loggedInUserRoleObj != null &&
                    !loggedInUsernameObj.toString().isEmpty() && loggedInUserRoleObj instanceof UserRole) {
                loggedIn = true;
            }
        }

        String contextPath = httpRequest.getContextPath();
        String userLoginURI = contextPath + "/user/login.xhtml";
        String requestedURI = httpRequest.getRequestURI();
        String pathWithinContext = requestedURI.substring(contextPath.length());

        boolean userLoginRequest = requestedURI.equals(userLoginURI);
        boolean resourceRequest = requestedURI.contains("/javax.faces.resource/");

        if (resourceRequest || userLoginRequest) {
            chain.doFilter(request, response);
            return;
        }


        if (FILTERED_PATHS.contains(pathWithinContext)) {
            if(loggedIn) {
                chain.doFilter(request, response);
            }
            else {
                httpResponse.sendRedirect(userLoginURI);
            }
        }
        else {
            chain.doFilter(request, response);
        }
    }
}
