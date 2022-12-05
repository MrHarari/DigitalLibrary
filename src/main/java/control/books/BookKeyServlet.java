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
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

@WebServlet(name = "BookKeyServlet", value = "/BookKeyServlet")
public class BookKeyServlet extends HttpServlet {
    // Recieves ISBN, email, and password of a user and book and returns the encrypted key of the book requested
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

        File keyFile = new File(DBUtil.PATH + "\\src\\main\\resources\\BookKey\\" + cc + ".key");
        FileInputStream is = new FileInputStream(keyFile);

        byte[] bytes = new byte[(int) keyFile.length()];
        is.read(bytes);

        try {
            String key = AESCrypt.GenKey();
            response.getOutputStream().write(Objects.requireNonNull(AESCrypt.encrypt(key, bytes)));

            File keyKeyFile = new File(DBUtil.PATH + "\\src\\main\\resources\\KeyKey\\" + cc + ".key");
            AESCrypt.saveKey(key, keyKeyFile);

            is.close();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
