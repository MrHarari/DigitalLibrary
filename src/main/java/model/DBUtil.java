package model;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;
import java.security.*;
import java.util.Objects;

public abstract class DBUtil {
    public static String PATH = "C:\\Users\\yonat\\IdeaProjects\\FinalProject";
    public static Connection cn;

    public static Connection GetConnection() throws ClassNotFoundException, SQLException {
        if (cn != null) if (!cn.isClosed()) return cn;

        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        return DriverManager.getConnection("jdbc:derby:" + PATH + "\\Library", "root", "root");
    }

    public static boolean UserExists(User u) throws SQLException, ClassNotFoundException, NoSuchAlgorithmException {
        if (u == null)
            return false;

        User temp = new User(u.username, u.email, Hash(u.password), u.auth);
        User other = GetUser(u.email);
        return temp.equals(other);
    }

    public static User GetUser(String email) throws SQLException, ClassNotFoundException {
        User u = null;
        try {
            cn = GetConnection();

            String query = "SELECT * FROM users WHERE EMAIL = ?";
            PreparedStatement st = cn.prepareStatement(query);
            st.setString(1, email);

            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String password = rs.getString("PASSWORD");
                String username = rs.getString("USERNAME");
                int auth = rs.getInt("AUTHORITY");
                u = new User(username, email, password, auth);
            }
            rs.close();
            st.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        return u;
    }

    public static int AddUser(String username, String email, String password) throws SQLException, ClassNotFoundException, NoSuchAlgorithmException {
        int result = 0;
        try {
            cn = GetConnection();
            PreparedStatement st1 = cn.prepareStatement("SELECT * FROM users WHERE EMAIL = ?");
            st1.setString(1, email);

            ResultSet rs = st1.executeQuery();

            if (rs.next()) {
                result = 1;
            } else {
                PreparedStatement st2 = cn.prepareStatement("INSERT INTO users (email, password, username) VALUES (?, ?, ?)");

                st2.setString(1, email);
                st2.setString(2, Hash(password));
                st2.setString(3, username);

                int rs2 = st2.executeUpdate();

                if (rs2 != 0) {
                    result = 2;
                }
            }

            rs.close();
            st1.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        return result;
    }

    public static Group GetGroup(String code) throws SQLException, ClassNotFoundException {
        Group group = null;

        try {
            cn = GetConnection();
            PreparedStatement st = cn.prepareStatement("SELECT * FROM GROUPS WHERE CODE = ?");
            st.setString(1, code);

            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String name = rs.getString("group_name");

                group = new Group(code, name);
            }

            st.close();
            rs.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        return group;
    }

    public static ArrayList<Group> GetAllGroups() throws SQLException, ClassNotFoundException {
        ArrayList<Group> groups = new ArrayList<>();

        try {
            cn = GetConnection();
            PreparedStatement st = cn.prepareStatement("SELECT * FROM GROUPS");

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                String code = rs.getString("code");
                String name = rs.getString("group_name");

                groups.add(new Group(code, name));
            }

            st.close();
            rs.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        return groups;
    }

    public static int JoinGroup(String code, String email) throws SQLException, ClassNotFoundException, NoSuchAlgorithmException {
        int result = 0;
        try {
            cn = GetConnection();
            PreparedStatement st1 = cn.prepareStatement("SELECT * FROM GROUPS WHERE CODE = ?");
            st1.setString(1, code);

            ResultSet rs = st1.executeQuery();

            if (rs.next()) {
                result = 1;

                String user2 = rs.getString("USER2");
                String user3 = rs.getString("USER3");
                String user4 = rs.getString("USER4");
                String user5 = rs.getString("USER5");

                String empty = "";

                if (Objects.equals(user2, "NA"))
                    empty = "USER2";
                else if (Objects.equals(user3, "NA"))
                    empty = "USER3";
                else if (Objects.equals(user4, "NA"))
                    empty = "USER4";
                else if (Objects.equals(user5, "NA"))
                    empty = "USER5";

                if (!empty.equals("")) {
                    PreparedStatement st2 = cn.prepareStatement("UPDATE GROUPS SET USER2 = ? WHERE CODE = ?");

                    st2.setString(1, email);
                    st2.setString(2, code);

                    st2.executeUpdate();
                    result = 2;
                }
            }

            rs.close();
            st1.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        return result;
    }

    public static ArrayList<Borrowed> GetLoan(String email) throws SQLException, ClassNotFoundException {
        ArrayList<Borrowed> loans = new ArrayList<>();

        try {
            cn = GetConnection();
            PreparedStatement st = cn.prepareStatement("SELECT * FROM loans WHERE EMAIL = ?");
            st.setString(1, email);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                String copy_code = rs.getString("copy_code");
                long date = rs.getLong("date_loaned");
                Borrowed b = new Borrowed(GetCopy(copy_code), GetUser(email), date);
                if (b.DaysPassed() > 9)
                    RemoveLoan(b.copy.copyCode);
                else
                    loans.add(new Borrowed(GetCopy(copy_code), GetUser(email), date));
            }

            st.close();
            rs.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        return loans;
    }

    public static Borrowed GetLoanCopyCode(String copyCode) throws SQLException, ClassNotFoundException {
        Borrowed l = null;

        try {
            cn = GetConnection();
            PreparedStatement st = cn.prepareStatement("SELECT * FROM loans WHERE COPY_CODE = ?");
            st.setString(1, copyCode);

            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String email = rs.getString("EMAIL");
                long date = rs.getLong("DATE_LOANED");
                l = new Borrowed(GetCopy(copyCode), GetUser(email), date);
            }

            st.close();
            rs.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        return l;
    }

    public static boolean RemoveLoan(String copyCode) throws SQLException, ClassNotFoundException {
        boolean result = false;
        try {
            cn = GetConnection();
            PreparedStatement st = cn.prepareStatement("DELETE FROM loans WHERE COPY_CODE= ?");
            st.setString(1, copyCode);

            int rs = st.executeUpdate();

            if (rs != 0) {
                st = cn.prepareStatement("UPDATE copies SET LOANED = 'false' WHERE COPY_CODE = ?");
                st.setString(1, copyCode);
                st.executeUpdate();

                result = true;
            }

            st.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        return result;
    }

    public static Copy GetCopy(String copyCode) throws SQLException, ClassNotFoundException {
        Copy c = null;
        try {
            cn = GetConnection();
            PreparedStatement st = cn.prepareStatement("SELECT * FROM copies WHERE COPY_CODE = ?");
            st.setString(1, copyCode);

            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String isbn = rs.getString("ISBN");
                boolean loaned = rs.getBoolean("LOANED");
                c = new Copy(GetBook(isbn), copyCode, loaned);
            }

            rs.close();
            st.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        return c;
    }

    public static ArrayList<Copy> GetCopyISBN(String isbn) throws SQLException, ClassNotFoundException {
        ArrayList<Copy> copies = new ArrayList<>();

        try {
            cn = GetConnection();
            PreparedStatement st = cn.prepareStatement("SELECT * FROM copies WHERE ISBN= ?");
            st.setString(1, isbn);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                String copyCode = rs.getString("COPY_CODE");
                boolean loaned = rs.getBoolean("LOANED");
                copies.add(new Copy(GetBook(isbn), copyCode, loaned));
            }

            st.close();
            rs.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        return copies;
    }

    public static int AddCopies(String isbn, String name, String author, String category, int year, int numOfCopies) throws SQLException, ClassNotFoundException {
        int result = 0;
        try {
            cn = GetConnection();
            PreparedStatement st;

            Book book = new Book(isbn, name, author, category, year);
            ArrayList<Copy> copies = MakeCopies(book, numOfCopies);

            for (Copy c : copies) {
                st = cn.prepareStatement("INSERT INTO copies VALUES (?, ?, 'false')");
                st.setString(1, c.copyCode);
                st.setString(2, isbn);

                st.executeUpdate();
            }

            result = 2;

            st = cn.prepareStatement("SELECT * FROM books WHERE ISBN= ?");
            st.setString(1, isbn);

            ResultSet rs = st.executeQuery();

            if (!rs.next()) {
                st = cn.prepareStatement("INSERT INTO books VALUES (?, ?, ?, ?, ?)");
                st.setString(1, isbn);
                st.setString(2, name);
                st.setString(3, author);
                st.setString(4, category);
                st.setInt(5, year);

                st.executeUpdate();
                result = 1;
            }

            rs.close();
            st.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        return result;
    }

    public static ArrayList<Copy> MakeCopies(Book book, int numOfCopies) throws SQLException, ClassNotFoundException {
        ArrayList<Copy> copies = new ArrayList<>();
        int max = MaxCopyCode();

        for (int i = 0; i < numOfCopies; i++)
            copies.add(new Copy(book, Integer.toString(max + i + 1), false));

        return copies;
    }

    public static int MaxCopyCode() throws SQLException, ClassNotFoundException {
        int max = 0;

        try {
            cn = GetConnection();
            Statement st = cn.createStatement();

            ResultSet rs = st.executeQuery("SELECT * FROM copies");

            while (rs.next()) {
                int copy_code = Integer.parseInt(rs.getString("copy_code"));
                if (copy_code > max)
                    max = copy_code;
            }

            st.close();
            rs.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }

        return max;
    }

    public static Book GetBook(String isbn) throws SQLException, ClassNotFoundException {
        Book b = null;
        try {
            cn = GetConnection();
            PreparedStatement st = cn.prepareStatement("SELECT * FROM books WHERE ISBN = ?");
            st.setString(1, isbn);

            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String bookName = rs.getString("BOOK_NAME");
                String authorName = rs.getString("AUTHOR");
                String category = rs.getString("CATEGORY");
                int publicationYear = rs.getInt("PUBLICATION_YEAR");
                b = new Book(isbn, bookName, authorName, category, publicationYear);
            }

            rs.close();
            st.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        return b;
    }

    public static ArrayList<Book> FindBooks(String desc) throws SQLException, ClassNotFoundException {
        ArrayList<Book> copies = new ArrayList<>();
        try {
            cn = GetConnection();
            Statement st = cn.createStatement();

            ResultSet rs = st.executeQuery("SELECT * FROM books");

            String[] pieces = desc.toLowerCase(Locale.ROOT).split(" ");

            while (rs.next()) {
                String isbn = rs.getString("isbn");
                String bookName = rs.getString("book_name").toLowerCase(Locale.ROOT);
                String authorName = rs.getString("author");
                String category = rs.getString("category");
                int publicationYear = rs.getInt("publication_year");

                for (String s : pieces) {
                    if (bookName.contains(s)) {
                        copies.add(new Book(isbn, bookName, authorName, category, publicationYear));
                    }
                }
            }

            rs.close();
            st.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        return copies;
    }

    public static int LoanBook(String copyCode, User u) throws SQLException, ClassNotFoundException, NoSuchAlgorithmException {
        int result = 0;
        if (!UserExists(u))
            return result;

        try {
            cn = GetConnection();
            PreparedStatement st = cn.prepareStatement("SELECT * FROM loans WHERE COPY_CODE = ?");
            st.setString(1, copyCode);

            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                result = 1;
            } else {
                if (GetUser(u.email) != null && GetCopy(copyCode) != null) {
                    st = cn.prepareStatement("UPDATE copies SET LOANED = 'true' WHERE COPY_CODE = ?");
                    st.setString(1, copyCode);
                    st.executeUpdate();

                    st = cn.prepareStatement("INSERT INTO loans VALUES (?, ?, ?)");
                    st.setString(1, copyCode);
                    st.setString(2, u.email);
                    st.setLong(3, System.currentTimeMillis());
                    int rs2 = st.executeUpdate();

                    if (rs2 != 0) {
                        result = 2;
                    }
                }
            }

            rs.close();
            st.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        return result;
    }

    public static String Hash(String str) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(str.getBytes());
        return new String(messageDigest.digest());
    }
}
