package control.webapp;

import model.DBUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.output.*;

@WebServlet(name = "BookServlet", value = "/BookServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 10,      // 10 MB
        maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
public class BookServlet extends HttpServlet {
    // Receives ISBN of a book and forwards to the correct page
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String isbn = request.getParameter("isbn");

        request.setAttribute("isbn", isbn);
        request.getRequestDispatcher("book.jsp").forward(request, response);
    }

    // Receives ISBN, name, author name, category name, year published, number of copies, contents and image of a book and adds it to the system
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String isbn = "", name = "", author = "", category = "";
        int year = 0, copies = 0;

        try {
            ServletFileUpload upload = new ServletFileUpload();
            response.setContentType("text/plain");

            FileItemIterator iterator = upload.getItemIterator(request);

            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();

                InputStream stream = item.openStream();

                if (item.isFormField()) {
                    switch (item.getFieldName()) {
                        case "isbn":
                            isbn = Streams.asString(stream);
                            break;
                        case "name":
                            name = Streams.asString(stream);
                            break;
                        case "author":
                            author = Streams.asString(stream);
                            break;
                        case "category":
                            category = Streams.asString(stream);
                            break;
                        case "year":
                            year = Integer.parseInt(Streams.asString(stream));
                            break;
                        case "copies":
                            copies = Integer.parseInt(Streams.asString(stream));
                            break;
                    }
                    System.out.println("Got a form field: " + item.getFieldName() + " " + item);
                } else {
                    File targetFile;

                    if (item.getFieldName().equals("image"))
                        targetFile = new File(DBUtil.PATH + "\\src\\main\\resources\\BookImages\\" + isbn + ".jpg");
                    else
                        targetFile = new File(DBUtil.PATH + "\\FinalProject\\src\\main\\resources\\BookPDF\\" + isbn + ".pdf");

                    if (targetFile.length() == 0){
                    OutputStream os = new FileOutputStream(targetFile);
                    int len;
                    byte[] buffer = new byte[8192];
                    while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
                        os.write(buffer, 0, len);
                    }
                    os.close();}
                }
            }
        } catch (FileUploadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        try {
            int rs = DBUtil.AddCopies(isbn, name, author, category, year, copies);
            response.setStatus(204);
        } catch (ClassNotFoundException e) {
            response.sendError(500);
        } catch (SQLException e) {
            response.sendError(409);
        }
    }
}
