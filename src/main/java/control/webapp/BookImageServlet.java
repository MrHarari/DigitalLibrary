package control.webapp;

import model.DBUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;

@WebServlet(name = "BookImageServlet", value = "/BookImageServlet")
public class BookImageServlet extends HttpServlet {
    // Receives ISBN of a book and returns its image
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String isbn =  request.getParameter("isbn");

        File targetFile = new File(DBUtil.PATH + "\\src\\main\\resources\\BookImages\\" + isbn + ".jpg");
        InputStream is = new FileInputStream(targetFile);

        int len;
        byte[] buffer = new byte[8192];
        while ((len = is.read(buffer, 0, buffer.length)) != -1) {
            response.getOutputStream().write(buffer, 0, len);
        }
        is.close();
    }
}
