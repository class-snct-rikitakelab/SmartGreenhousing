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
    ���[�U���F<input type="text" name="user" size="20">
    ���b�Z�[�W�F<input type="text" name="message" size="60">
    <input type="button" value="���M" 
      onclick="postMessage(user.value, message.value,
      '../CometServlet', true)">
  </form>
</body>
</html>