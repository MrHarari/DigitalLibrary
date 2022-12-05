<%@ page import="java.io.File" %>
<%@ page import="org.apache.pdfbox.pdmodel.PDDocument" %>
<%@ page import="org.apache.pdfbox.Loader" %>
<%@ page import="org.apache.pdfbox.multipdf.Splitter" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: yonat
  Date: 3/25/2022
  Time: 3:52 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<%
    File targetFile = new File("C:\\Users\\yonat\\IdeaProjects\\FinalProject\\src\\main\\resources\\BookPDF\\" + request.getParameter("isbn") + ".pdf");
    PDDocument document = Loader.loadPDF(targetFile);

    int pages = document.getNumberOfPages();
%>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My PDF Viewer</title>
    <link rel="stylesheet" type="text/css" href="CSS/pdf-viewer.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/pdf.js/2.0.943/pdf.min.js"></script>
    <script src="https://code.jquery.com/jquery-2.0.3.min.js"></script>

</head>
<body>
<div id="my_pdf_viewer">
    <div class="navbar">
        <div id="navigation_controls">
            <button id="go_previous">←</button>
            <input id="current_page" value="1" type="number" min="1" max="<%= pages %>"/>
            <button id="go_next">→</button>
        </div>

        <div id="zoom_controls">
            <button id="zoom_in">+</button>
            <button id="zoom_out">-</button>
        </div>
    </div>

    <div id="canvas_container">
        <canvas id="pdf_renderer"></canvas>
    </div>
</div>
<script>
    var myState = {
        pdf: null,
        currentPage: 1,
        zoom: 1
    }

    render();

    function render() {
        console.log(myState.currentPage);
        pdfjsLib.getDocument('BookContentServlet?isbn=<%= request.getParameter("isbn") %>&index=' + myState.currentPage + '').then((pdf) => {
            myState.pdf = pdf;
            myState.pdf.getPage(1).then((page) => {
                var canvas = document.getElementById("pdf_renderer");
                var ctx = canvas.getContext('2d');

                var viewport = page.getViewport(myState.zoom);

                canvas.width = viewport.width;
                canvas.height = viewport.height;

                page.render({
                    canvasContext: ctx,
                    viewport: viewport
                });
            });
        });
    }

    $('#go_previous').click(function (e) {
        if (myState.pdf == null || myState.currentPage === 1)
            return;

        myState.currentPage -= 1;
        $('#current_page').val(myState.currentPage);
        render();
    });

    $('#go_next').click(function (e) {
        if (myState.pdf == null || myState.currentPage >= <%= pages %>)
            return;

        myState.currentPage += 1;
        $('#current_page').val(myState.currentPage);
        render();
    });

    $('#current_page').keypress(function (e) {
        if (myState.pdf == null) return;


        var keycode = (e.keyCode ? e.keyCode : e.which);
        console.log(keycode.type);
        console.log(keycode);

        if (keycode == '13') {
            var desiredPage = parseInt($('#current_page').val());
            console.log(desiredPage);

            if (desiredPage >= 1 && desiredPage <= <%= pages %>) {
                myState.currentPage = desiredPage;
                $('#current_page').val(desiredPage);
                render();
            }
        }
    });

    $('#zoom_in').click(function (e) {
        if (myState.pdf == null) return;

        myState.zoom *= 1.5;

        myState.pdf.getPage(1).then((page) => {
            var canvas = document.getElementById("pdf_renderer");
            var ctx = canvas.getContext('2d');

            var viewport = page.getViewport(myState.zoom);

            canvas.width = viewport.width;
            canvas.height = viewport.height;

            page.render({
                canvasContext: ctx,
                viewport: viewport
            });
        });
    });

    $('#zoom_out').click(function (e) {
        if (myState.pdf == null) return;

        myState.zoom *= 2 / 3;

        myState.pdf.getPage(1).then((page) => {
            var canvas = document.getElementById("pdf_renderer");
            var ctx = canvas.getContext('2d');

            var viewport = page.getViewport(myState.zoom);

            canvas.width = viewport.width;
            canvas.height = viewport.height;

            page.render({
                canvasContext: ctx,
                viewport: viewport
            });
        });
    });
</script>
</body>
</html>
