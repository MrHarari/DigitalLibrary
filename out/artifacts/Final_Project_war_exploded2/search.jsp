<%--
  Created by IntelliJ IDEA.
  User: yonat
  Date: 3/10/2022
  Time: 7:59 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="model.*" %>
<%@ page import="java.sql.SQLException" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean scope="request" id="desc" type="java.lang.String"/>
<%! User u;%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Library</title>
    <link rel="icon" type="image/png" href="IMAGES/icon.png">
    <link rel="stylesheet" type="text/css" href="CSS/style.css">
    <script src="https://code.jquery.com/jquery-2.0.3.min.js"></script>
</head>
<body style="background: url('IMAGES/background.jpg');">
<%
    u = (User) session.getAttribute("User");
    try {
        if (u.auth != 0 && !DBUtil.UserExists(u)) {
%>
<script>
    alert('You must be logged in!');
    window.location.href = "index.jsp";
</script>
<%
            return;
        }
    } catch (SQLException | ClassNotFoundException e) {
        e.printStackTrace();
    }
%>

<div class="topnav">
    <span class="active"><a href="welcome.jsp">Home</a></span>
    <form action="SearchServlet">
        <label><input class="search" type="submit" id="submit-search" value="Search"></label>
        <label><input name="description" class="search" type="text" placeholder="Description"></label>
    </form>
    <% if (u.auth == 0) { %>
    <span>Hello <%= u.username %>!</span>
    <% } else { %>
    <span>Hello <a href="profile.jsp" id="hello"><%= u.username %></a>!</span>
    <% } %>
</div>

<br>
<h1 class="text" style="text-align:center">Welcome</h1>
<br>
<div class="text">
    <table>
        <tr>
            <th>ISBN</th>
            <th>Book Name</th>
            <th>Author Name</th>
            <th>Category</th>
            <th>Publication Year</th>
            <th>Action</th>
        </tr>
        <%
            for (Book b : DBUtil.FindBooks(desc)) {
        %>
        <tr>
            <td><%= b.isbn %>
            </td>
            <td><%= b.bookName %>
            </td>
            <td><%= b.author %>
            </td>
            <td><%= b.category %>
            </td>
            <td><%= b.publicationYear %>
            </td>

            <td>
                <form id="loan" action="BookServlet">
                    <input type="hidden" name="isbn" value="<%= b.isbn %>">
                    <input type="submit" value="Book Page">
                </form>
            </td>
        </tr>
        <% } %>
    </table>
</div>
</body>
</html>
