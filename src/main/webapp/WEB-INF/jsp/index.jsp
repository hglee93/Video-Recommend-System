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
        function showRecInfo(obj) {
            var div = document.querySelector('#recommend');

            var html = "";
            html += "<table>";
            for(var i = 0; i < obj.length / 5; i++) {
                html += "<tr>";
                for(var j = i * 5 ; j < (i * 5) + 5 ; j++){
                    var jsonobj = JSON.parse(JSON.stringify(obj[j]));
                    html += "<td width='100'><a href=/content?videoId=" + jsonobj.videoId + "><img src=img/video.png width=150 height=150></a></td>";
                }
                html += "</tr>";
                html += "<tr>";
                for(var j = i * 5 ; j < (i * 5) + 5 ; j++){
                    var jsonobj = JSON.parse(JSON.stringify(obj[j]));
                    var title = jsonobj.videoTitle;
                    if(jsonobj.videoTitle.length > 30) {
                        title = jsonobj.videoTitle.substring(0, 30);
                        title += "...";
                    }
                    html += "<td width='100'>" + title + "</td>";
                }
                html += "</tr>";
            }
            html += "</table>";
            div.innerHTML = html;
        }

        function showVideoInfo(obj) {
            var div = document.querySelector('#list');

            var html = "";
            html += "<table>";
            for(var i = 0; i < obj.length / 5; i++) {
                html += "<tr>";
                for(var j = i * 5 ; j < (i * 5) + 5 ; j++){
                    var jsonobj = JSON.parse(JSON.stringify(obj[j]));
                    html += "<td width='100'><a href=/content?videoId=" + jsonobj.videoId + "><img src=img/video.png width=150 height=150></a></td>";
                }
                html += "</tr>";
                html += "<tr>";
                for(var j = i * 5 ; j < (i * 5) + 5 ; j++){
                    var jsonobj = JSON.parse(JSON.stringify(obj[j]));
                    var title = jsonobj.videoTitle;
                    if(jsonobj.videoTitle.length > 30) {
                        title = jsonobj.videoTitle.substring(0, 30);
                        title += "...";
                    }
                    html += "<td width='100'>" + title + "</td>";
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
            url:"/video/recommend?userId=1",
            dataType:"json",
            success:function(data){
                <!-- console.log(data);-->
                showRecInfo(data);
            }
        });

        $.ajax({
            type:"get",
            url:"/video/list",
            dataType:"json",
            success:function(data){
                showVideoInfo(data);
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
<div>
    <div style="border-bottom: 1px solid gray" align="center"><h2>추천동영상</h2></div>
    <div id="recommend" width="70%" align="center">
    </div>
    <div style="border-bottom: 1px solid gray" align="center"><h2>일반동영상</h2></div>
    <div id="list" width="70%" align="center">
    </div>
</div>
</body>
</html>