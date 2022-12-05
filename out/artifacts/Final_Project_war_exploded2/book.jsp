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
<jsp:useBean scope="request" id="isbn" type="java.lang.String"/>
<%! User u; %>
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
    Book b = DBUtil.GetBook(isbn);
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
<h1 class="text" style="text-align:center"><%= b.bookName %>></h1>
<br>
<div class="text">
    <table>
        <tr>
            <td style="width: 30%">
                <img src="BookImageServlet?isbn=<%= b.isbn %>" alt="Book Cover" style="width: 25%">
            </td>
            <td>
                <span><strong>ISBN</strong> - <%= b.isbn %></span><br>
                <span><strong>Name</strong> - <%= b.bookName %></span><br>
                <span><strong>Author</strong> - <%= b.author %></span><br>
                <span><strong>Category</strong> - <%= b.category %></span><br>
                <span><strong>Publication Year</strong> - <%= b.publicationYear %></span><br>
            </td>
        </tr>
    </table>

    <br>

    <table>
        <tr>
            <th>Copy Code</th>
            <th>Loaned</th>
            <th>Action</th>
        </tr>
        <%
            for (Copy c : DBUtil.GetCopyISBN(isbn)) {
        %>
        <tr>
            <td><%= c.copyCode %>
            </td>
            <td><%= c.loaned %>
            </td>


            <% if (u.auth != 0 && !c.loaned) { %>
            <td>
                <form id="loan" action="LoanServlet">
                    <input type="hidden" name="cc" value="<%= c.copyCode %>">
                    <input type="submit" value="loan">
                </form>
                <script>
                    $("#loan").bind("submit", function (e) {
                        e.preventDefault();
                        $.ajax({
                            type: "Get",
                            url: "LoanServlet",
                            data:
                                {
                                    cc: $("input[name='cc']").val(),
                                },
                            error: function (xhr, ajaxOptions, thrownError) {
                                console.log(xhr.status);
                                console.log(thrownError);
                            },
                            success: function (res) {
                                window.location.href = "";
                            }
                        });
                    });
                </script>
            </td>
            <% } %>
        </tr>
        <% } %>
    </table>
</div>
</body>
</html>
