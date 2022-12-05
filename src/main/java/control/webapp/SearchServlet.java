package control.webapp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "SearchServlet", value = "/SearchServlet")
public class SearchServlet extends HttpServlet {
    // Receives description of a book and forwards to a list of books that match
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String desc = request.getParameter("description");

        request.setAttribute("desc", desc);
        request.getRequestDispatcher("search.jsp").forward(request, response);
    }
}
