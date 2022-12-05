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
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>JSP Page</title>
    <link rel="stylesheet" type="text/css" href="CSS/chat.css">
</head>
<body>
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
<div class="header">
    <h1>Live Chat updates</h1>
</div>

<div id="text">
    <div id="chat" class="chat"></div>
    <div class="bottom">
        <input type="text" name="msg" id="msg" placeholder="Enter message here"/>
        <button onclick="return sendMsg();">Enter</button>
    </div>
</div>
<div id="groups">
</div>

<script type="text/javascript">
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
        mySpan.innerHTML += "<strong>" + user + " - </strong>" + message + "<br/>";
    };

    ws.onerror = function (event) {
        console.log("Error ", event)
    }

    function sendMsg() {
        var msg = document.getElementById("msg").value;
        if (msg) {
            ws.send(msg);
            document.getElementById("chat").innerHTML += "<strong>You - </strong>" + msg + "<br/>";
        }
        document.getElementById("msg").value = "";
    }
</script>
</body>
</html>