<%--
  Created by IntelliJ IDEA.
  User: yonat
  Date: 3/10/2022
  Time: 7:59 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="model.*" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.security.NoSuchAlgorithmException" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
        if (u == null || (u.auth != 0 && !DBUtil.UserExists(u))) {
%>
<script>
    alert('You must be logged in!');
    window.location.href = "index.jsp";
</script>
<%
            return;
        }
    } catch (SQLException | ClassNotFoundException | NoSuchAlgorithmException e) {
        e.printStackTrace();
    }
%>

<div class="topnav">
    <span class="active"><a href="welcome.jsp">Home</a></span>
    <form action="SearchServlet">
        <label><input class="search" type="submit" id="submit-search" value="Search"></label>
        <label><input name="description" class="search" type="text" placeholder="Description"></label>
    </form>
    <span>Hello <%= u.username %>!</span>
    <% if (u.auth != 0) { %>
    <span>Click to join <a href="chat.jsp">chat</a></span>
    <% } %>
    <span><a href="index.jsp" id="leave">log out</a></span>
</div>

<br>
<h1 class="text" style="text-align:center">Profile</h1>
<br>
<div class="text">
    <a href="client.zip"><strong>Click to download client</strong></a><br><br>

    <span><strong>Username</strong> - <%= u.username %></span><br>
    <span><strong>Email</strong> - <%= u.email %></span><br>

    <h2> Borrowed books: </h2>
    <table>
        <tr>
            <th>Copy Code</th>
            <th>ISBN</th>
            <th>Book Name</th>
            <th>Time Borrowed</th>
            <th>Action</th>
        </tr>

        <% for (Borrowed l : DBUtil.GetLoan(u.email)) { %>
        <tr id="<%= l.copy.copyCode %>">
            <td><%= l.copy.copyCode %>
            </td>
            <td><%= l.copy.book.isbn %>
            </td>
            <td><%= l.copy.book.bookName %>
            </td>
            <td><%= new java.sql.Date(l.timeBorrowed) %>
            </td>

            <td>
                <form id="remove-loan<%= l.copy.copyCode %>" method="post" action="LoanServlet">
                    <input type="hidden" name="cc" value="<%= l.copy.copyCode %>">
                    <input type="submit" value="delete">
                </form>
                <script>
                    $("#remove-loan<%= l.copy.copyCode %>").bind("submit", function (e) {
                        e.preventDefault();
                        $.ajax({
                            type: "Post",
                            url: "LoanServlet",
                            data:
                                {
                                    cc: <%= l.copy.copyCode %>,
                                },
                            error: function (xhr, ajaxOptions, thrownError) {
                                console.log(xhr.status);
                                console.log(thrownError);
                            },
                            success: function (res) {
                                $("#<%= l.copy.copyCode %>").remove()
                            }
                        });
                    });
                </script>
            </td>
        </tr>
        <% } %>
    </table>

    <% if (u.auth == 2) { %>
    <h2>Add book: </h2>
    <form id="add-book" action="BookServlet" method="post" enctype="multipart/form-data">
        <table>
            <tr>
                <td><label for="isbn" class="label">ISBN - </label></td>
                <td><label for="name" class="label">Name - </label></td>
                <td><label for="author" class="label">Author Name - </label></td>
                <td><label for="category" class="label">Category - </label></td>
                <td><label for="year" class="label">Publication Year - </label></td>
                <td><label for="copies" class="label">Copies - </label></td>
                <td><label for="image" class="label">Image - </label></td>
                <td><label for="content" class="label">File - </label></td>
            </tr>
            <tr>
                <td><input class="input" type="text" id="isbn" name="isbn"
                           placeholder="Enter the ISBN of the book"></td>
                <td><input class="input" type="text" id="name" name="name"
                           placeholder="Enter the name of the book"></td>
                <td><input class="input" type="text" id="author" name="author"
                           placeholder="Enter the author's name"></td>
                <td><input class="input" type="text" id="category" name="category"
                           placeholder="Enter the category"></td>
                <td><input class="input" type="number" id="year" name="year"
                           placeholder="Enter the publication year"></td>
                <td><input class="input" type="number" id="copies" name="copies" min="1"
                           placeholder="Enter the number of copies"></td>

                <td><input class="input" type="file" id="image" name="image"></td>
                <td><input class="input" type="file" id="content" name="content"></td>
            </tr>
        </table>
        <br>
        <input type="submit" value="Add Book">
    </form>
    <script>
        $("#add-book").bind("submit", function (e) {
            let image_name = $("#image").val();
            let content_name = $("#content").val();

            if (image_name.length === 0)
                image_name = "jpg";
            else {
                image_name = image_name.substr(image_name.lastIndexOf(".") + 1);
                console.log(image_name);
            }

            if (content_name.length === 0)
                content_name = "pdf";
            else {
                content_name = content_name.substr(content_name.lastIndexOf(".") + 1);
                console.log(content_name);
            }

            if (content_name !== "pdf" || image_name !== "jpg") {
                alert("wrong file format!");
                e.preventDefault();
            }
        });
    </script>
    <% } %>
</div>
</body>
</html>
