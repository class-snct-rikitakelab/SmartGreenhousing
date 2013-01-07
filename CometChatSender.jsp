<%@ page language="java" contentType="text/html; charset=UTF-8" 
  pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>TestCometChat Sender</title>
  <script type="text/javascript" src="../js/common.js"></script>
</head>
<body>
  <form method="POST" action="--WEBBOT-SELF--">
    ユーザ名：<input type="text" name="user" size="20">
    メッセージ：<input type="text" name="message" size="60">
    <input type="button" value="送信" 
      onclick="postMessage(user.value, message.value,
      '../CometServlet', true)">
  </form>
</body>
</html>