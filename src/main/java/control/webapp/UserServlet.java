package control.webapp;

import model.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

@WebServlet(name = "UserServlet", value = "/UserServlet")
public class UserServlet extends HttpServlet {
    // Receives email and password and returns whether that account exists
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession s = request.getSession();
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            User u = new User("", email, DBUtil.Hash(password), 0);
            User temp = DBUtil.GetUser(email);
            if (u.equals(temp)) {
                s.setAttribute("User", new User(temp.username, temp.email, password, temp.auth));
                response.setStatus(204);
            } else {
                response.sendError(409);
            }
        } catch (SQLException | ClassNotFoundException | NoSuchAlgorithmException e) {
            response.sendError(500);
            e.printStackTrace();
        }
    }

    // Receives username email and password and adds the user to the system
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        int rt;
        try {
            rt = DBUtil.AddUser(username, email, password);
        } catch (Exception e) {
            response.setStatus(500);
            return;
        }

        if (rt == 0) {
            response.setStatus(500);
        } else if (rt == 1) {
            response.setStatus(409);
        } else if (rt == 2) {
            response.setStatus(204);
        }
    }
}
