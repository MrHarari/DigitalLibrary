<%--
    Document   : index
    Created on : Jun 11, 2020, 7:06:53 PM
    Author     : Learn new here
--%>

<%@ page import="model.*" %>
<%@ page import="java.sql.SQLException" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%! User u; %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Library</title>
    <link rel="icon" type="image/png" href="IMAGES/icon.png">
    <script src="https://code.jquery.com/jquery-2.0.3.min.js"></script>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="CSS/style.css">
    <link rel="stylesheet" type="text/css" href="CSS/chat.css">
</head>
<body>
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
    <span><a href="index.jsp" id="leave">log out</a></span>
</div>

<div class="header">
    <h1 id="head">You haven't joined a group yet</h1>
</div>

<div id="text">
    <div id="chat" class="chat"></div>
    <div class="bottom">
        <input type="text" name="msg" id="msg" placeholder="Enter message here"/>
        <button onclick="return sendMsg();">Enter</button>
    </div>
</div>
<div id="groups">
    <table>
        <tr>
            <th>Groups</th>
            <th>Action</th>
        </tr>
        <% for (Group g : DBUtil.GetAllGroups()) { %>
        <tr>
            <td><%= g.name %>
            </td>
            <td>
                <button onclick="return join(<%=g.code%>, '<%=g.name%>');">Enter</button>
            </td>
        </tr>
        <% } %>
    </table>
</div>

<script type="text/javascript">
    var groupCode;
    var wsUrl;
    if (window.location.protocol == 'http:') {
        wsUrl = 'ws://';
    } else {
        wsUrl = 'wss://';
    }
    var ws = new WebSocket(wsUrl + window.location.host + "/Final_Project_war_exploded/chat?email=<%=u.email%>&pw=<%=u.password%>");

    ws.onmessage = function (event) {
        var mySpan = document.getElementById("chat");
        var data = event.data;
        var user = data.split(';')[0];
        var message = data.substring(data.indexOf(';') + 1);
        mySpan.innerHTML += "<p><strong>" + user + " - </strong>" + message + "</p>";
    };

    ws.onerror = function (event) {
        console.log("Error ", event)
    }

    function sendMsg() {
        var msg = document.getElementById("msg").value;
        if (msg) {
            ws.send("MSG;" + msg);
            console.log("MSG;" + msg);
            document.getElementById("chat").innerHTML += "<p><strong>You - </strong>" + msg + "</p>";
        }
        document.getElementById("msg").value = "";
    }

    function join(code, name) {
        if (code !== groupCode) {
            console.log("JOIN;" + code)
            ws.send("JOIN;" + code);
            $("#chat").text("")
            $("#head").text(name);
            groupCode = code
        }
    }
</script>
</body>
</html>