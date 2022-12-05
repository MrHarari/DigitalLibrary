package control.books;

import model.AESCrypt;
import model.Borrowed;
import model.DBUtil;
import model.User;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;

import java.io.File;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@WebServlet(name = "BookContentServlet", value = "/BookContentServlet")
public class BookContentServlet extends HttpServlet {
    // Recieves ISBN, email, and password of a user and book and returns the encrypted contents of the book requested
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

        File targetFile = new File(DBUtil.PATH + "\\src\\main\\resources\\BookPDF\\" + isbn + ".pdf");

        try {
            String key = AESCrypt.GenKey();
            byte[] bytes = new byte[(int)targetFile.length()];
            FileInputStream is = new FileInputStream(targetFile);
            is.read(bytes);
            response.getOutputStream().write(Objects.requireNonNull(AESCrypt.encrypt(key, bytes)));

            File keyFile = new File(DBUtil.PATH + "\\src\\main\\resources\\BookKey\\" + cc + ".key");
            AESCrypt.saveKey(key, keyFile);

            is.close();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
