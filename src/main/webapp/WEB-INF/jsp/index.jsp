<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <link href="https://fonts.googleapis.com/css?family=Nanum+Gothic|Roboto" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="css/template.css">
    <link rel="stylesheet" href="/webjars/jquery-ui/1.11.4/jquery-ui.min.css" />
    <script src="/webjars/jquery/1.11.1/jquery.min.js"></script>
    <script src="/webjars/jquery-ui/1.11.4/jquery-ui.min.js"></script>

    <script>
        function showempinfo(obj) {
            var div = document.querySelector('#recommend');

            var html = "";
            html += "<table>";
            for(var i = 0; i < obj.length / 5; i++) {
                html += "<tr>";
                for(var j = i * 5 ; j < (i * 5) + 5 ; j++){
                    var jsonobj = JSON.parse(JSON.stringify(obj[j]));
                    html += "<td><a href=/content?videoId=" + jsonobj.videoId + "><img src=img/video.png width=150 height=150></a></td>";
                }
                html += "</tr>";
                html += "<tr>";
                for(var j = i * 5 ; j < (i * 5) + 5 ; j++){
                    var jsonobj = JSON.parse(JSON.stringify(obj[j]));
                    html += "<td>" + jsonobj.videoTitle + jsonobj.videoTitle.length+ "</td>";
                }
                html += "</tr>";
            }
            html += "</table>";
            div.innerHTML = html;
        }
    </script>
    <script>
        $.ajax({
            type:"get",
            url:"/video/list?userId=1",
            dataType:"json",
            success:function(data){
                <!-- console.log(data);-->
                showempinfo(data);
            }
        });
    </script>

</head>
<body>
<div id="nav">
    <ul>
        <li>
            <a href="index.jsp">
                <span>MAIN PAGE</span>
            </a>
        </li>
    </ul>
</div>
</div>
<div id="recommend" width="100%" align="center">
</div>
<div id="content" width="100%" align="center">
</div>
</body>
</html>