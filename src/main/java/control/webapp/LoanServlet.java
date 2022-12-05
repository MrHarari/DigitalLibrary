package control.webapp;

import model.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

@WebServlet(name = "LoanServlet", value = "/LoanServlet")
public class LoanServlet extends HttpServlet {
    // Receives copy code of a book and a user and loans the book if the user can
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String copyCode = request.getParameter("cc");
        User u = (User) request.getSession().getAttribute("User");


        try {
            if (!(DBUtil.GetLoan(u.email).size() >= 3)) {
                int rs = DBUtil.LoanBook(copyCode, u);
                if (rs == 2)
                    response.setStatus(204);
                else
                    response.setStatus(409);
            } else {
                response.setStatus(409);
            }
        } catch (ClassNotFoundException e) {
            response.sendError(500);
        } catch (SQLException | NoSuchAlgorithmException e) {
            response.sendError(409);
            request.getRequestDispatcher("index.jsp");
        }
    }

    // Receives copy code of a book and a user and removes the loan
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String copyCode = request.getParameter("cc");
        User u = (User) request.getSession().getAttribute("User");
        try {
            if (!DBUtil.UserExists(u)) {
                response.sendError(401);
                return;
            }
        } catch (SQLException | ClassNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            response.sendError(409);
            return;
        }

        try {
            boolean rs = DBUtil.RemoveLoan(copyCode);
            if (rs)
                response.setStatus(204);
            else
                response.setStatus(409);
        } catch (ClassNotFoundException e) {
            response.sendError(500);
        } catch (SQLException e) {
            response.sendError(409);
        }

        try {
            request.setAttribute("isbn", DBUtil.GetCopy(copyCode).book.isbn);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
