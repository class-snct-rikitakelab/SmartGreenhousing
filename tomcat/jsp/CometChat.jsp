<%@ page language="java" contentType="text/html; charset=UTF-8" 
  pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>TestCometChat</title>
</head>
<frameset rows="90%,10%">
  <frame name="display_messages" src="../CometServlet">
  <frame name="message_sender" src="CometChatSender.jsp">
</frameset>
</html>