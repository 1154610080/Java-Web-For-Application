<%--
  Created by IntelliJ IDEA.
  User: Egan
  Date: 2018/9/1
  Time: 8:46
  To change this template use File | Settings | File Templates.
--%>
<%--@elvariable id="currentUser" type="com.web.site.User"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>User Home</title>
</head>
<body>
    ID: ${currentUser.userId} <br/>
    Username: ${currentUser.username} <br/>
    Name: ${currentUser.name} <br/>
</body>
</html>
