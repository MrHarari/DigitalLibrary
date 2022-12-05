package control.books;

import model.Borrowed;
import model.DBUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "GetLoansServlet", value = "/GetLoansServlet")
public class GetLoansServlet extends HttpServlet {
    // Recieves email of a user and returns a list of all books loaned by said user
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        OutputStream os = response.getOutputStream();

        try {
            ArrayList<Borrowed> loans = DBUtil.GetLoan(email);

            for (Borrowed l : loans) {
                if (l.DaysPassed() > 9)
                    DBUtil.RemoveLoan(l.copy.copyCode);
                os.write((l.copy.book.bookName + '&' + l.copy.book.isbn + '&' + l.timeBorrowed + ';').getBytes(StandardCharsets.UTF_8));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
