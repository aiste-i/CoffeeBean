package org.coffee.servlet;

import org.coffee.persistence.entity.Business;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/business/change-email")
public class BusinessChangeEmailServlet extends HttpServlet {
    // inject the same PU from persistence.xml
    @PersistenceContext(unitName = "CoffeeBeanPU")
    private EntityManager em;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/changeEmail.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email    = req.getParameter("email");
        String confirm  = req.getParameter("confirmEmail");
        String password = req.getParameter("password");

        HttpSession session = req.getSession(false);
        // <-- make sure this matches however you store the logged-in business
        Business business = (Business) session.getAttribute("business");

        String error = null;
        if (!email.equals(confirm)) {
            error = "Emails don’t match";
        } else if (!email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            error = "Invalid email format";
        } else if (!business.checkPassword(password)) {
            error = "Wrong password";
        }

        if (error != null) {
            req.setAttribute("error", error);
            doGet(req, resp);
            return;
        }

        // update and save
        business.setEmail(email);
        em.merge(business);

        // redirect somewhere sensible
        resp.sendRedirect(req.getContextPath() + "/business/profile?updated=true");
    }
}
