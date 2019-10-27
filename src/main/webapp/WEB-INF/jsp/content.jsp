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
        function like(videoId, userId) {
            $.ajax({
                type:"get",
                url:"/like?userId=1&videoId=" + videoId,
                dataType:"json",
                success:function(data){
                    alert("좋아요를 누르셨습니다.");
                }
            });
        }
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
<div width="100%" align="center">
    <div>
        <video width="1000px" width="500px" controls src="/download?fileName=test.mp4">
            not use video
        </video>
    </div>
    <div><h3>${videoTitle}</h3><button type="button" onclick="like(${videoId}, ${userId})">좋아요</button></div>
</div>
</body>
</html>