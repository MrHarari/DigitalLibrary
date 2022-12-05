<%--
  Created by IntelliJ IDEA.
  User: yonat
  Date: 3/10/2022
  Time: 7:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="model.*" %>
<%@ page import="java.sql.SQLException" %>
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
        if (u == null || (!DBUtil.UserExists(u) && u.auth != 0)) {
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
    <span class="active">Home</span>
    <form action="SearchServlet">
        <label><input class="search" type="submit" id="submit-search" value="Search"></label>
        <label><input name="description" class="search" type="text" placeholder="Description"></label>
    </form>
    <% if (u.auth == 0) { %>
    <span>Hello <%= u.username %>!</span>
    <% } else { %>
    <span>Hello <a href="profile.jsp" id="hello"><%= u.username %></a>!</span>
    <span>Click to join <a href="chat.jsp">chat</a></span>
    <% } %>
    <span><a href="index.jsp" id="leave">log out</a></span>
</div>
<br>
<h1 class="text" style="text-align:center">Welcome</h1>
</body>
</html>