package control.books;

import model.AESCrypt;
import model.Borrowed;
import model.DBUtil;
import model.User;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import static org.apache.commons.codec.binary.Hex.encodeHex;

@WebServlet(name = "KeyKeyServlet", value = "/KeyKeyServlet")
public class KeyKeyServlet extends HttpServlet {
    // Receives ISBN, email, and password of a user and book and returns the key to the encrypted key of the book
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String isbn = request.getParameter("isbn");
        String email = request.getParameter("email");
        String pw = request.getParameter("pw");
        User u = new User("", email, pw, 0);

        String cc = "";
        try {
            if (!DBUtil.UserExists(u)) {
                response.sendError(404);
                return;
            }

            ArrayList<Borrowed> loans = DBUtil.GetLoan(u.email);
            boolean loaned = false;

            for (Borrowed l : loans) {
                if (l.copy.book.isbn.equals(isbn)) {
                    cc = l.copy.copyCode;
                    loaned = true;
                    break;
                }
            }

            if (!loaned) {
                response.sendError(404);
                return;
            }

        } catch (SQLException | ClassNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            response.sendError(404);
            return;
        }

        try {
            response.getOutputStream().print(new String(Files.readAllBytes(Paths.get(DBUtil.PATH + "\\src\\main\\resources\\KeyKey\\" + cc + ".key"))));
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500);
        }
    }
}
