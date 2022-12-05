package control.webapp;

import model.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "GuestServlet", value = "/GuestServlet")
public class GuestServlet extends HttpServlet {
    // Receives request to join as a guest and forwards the user as a guest
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession().setAttribute("User", new User("Guest", "", "", 0));
        request.getRequestDispatcher("welcome.jsp").forward(request, response);
    }
}
