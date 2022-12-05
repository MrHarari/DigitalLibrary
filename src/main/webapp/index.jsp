<%--
  Created by IntelliJ IDEA.
  User: yonat
  Date: 3/6/2022
  Time: 6:42 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Library</title>
    <link rel="icon" type="image/png" href="IMAGES/icon.png">
    <link rel="stylesheet" type="text/css" href="CSS/style.css">
    <link rel="stylesheet" type="text/css" href="CSS/modal.css">
    <script src="https://code.jquery.com/jquery-2.0.3.min.js"></script>
</head>

<body style="background: url('IMAGES/background.jpg');">

<% session.removeAttribute("User"); %>

<div id="modal">
    <div class="modal">
        <div class="content" style="height: 43%;">
            <h1>Login</h1>
            <hr>
            <form id="login-form">
                <img class="login-image" src="IMAGES/login-icon.png">
                <div style="width: 34%;">
                    <label for="email">Email</label><br><input type="email" placeholder="Enter Email" name="email"
                                                               id="email" required><br>
                    <label for="password">Password</label><br><input type="password" placeholder="Enter Password"
                                                                     name="password" id="password" required><br>

                    <div class="bottom">
                        <hr>
                        <input id="submit-login" type="submit" value="Login">
                        <a id="signup" type="button" href="register.jsp">Signup</a>
                        <br>
                        <a href="GuestServlet">Login as guest!</a>
                    </div>
                </div>
            </form>
            <script>
                async function sha256(message) {
                    const msgBuffer = new TextEncoder().encode(message);
                    const hashBuffer = await crypto.subtle.digest('SHA-256', msgBuffer);
                    const hashArray = Array.from(new Uint8Array(hashBuffer));
                    return hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
                }

                $("#login-form").bind("submit", function (e) {
                    e.preventDefault();
                    sha256($("input[name='password']").val()).then(function (result) {
                        $.ajax({
                            type: "GET",
                            url: "UserServlet",
                            data:
                                {
                                    email: $("input[name='email']").val(),
                                    password: result,
                                },
                            error: function (xhr, ajaxOptions, thrownError) {
                                if (xhr.status === 500)
                                    alert('Something went wrong....')
                                if (xhr.status === 409)
                                    alert('Email or password are wrong!')
                                console.log(xhr.status);
                                console.log(thrownError);
                            },
                            success: function (res) {
                                window.location.href = "welcome.jsp";
                            }
                        });
                    });
                });
            </script>
        </div>
    </div>
</div>
</body>
</html>