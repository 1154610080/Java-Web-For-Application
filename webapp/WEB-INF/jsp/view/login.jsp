<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--@elvariable id="loginFailed" type="java.lang.Boolean"--%>
<%--@elvariable id="loginForm" type="com.web.site.AuthenticationFiler.Form"--%>
<template:loggedOut htmlTitle="Log In" bodyTitle="Log In">
    You must log in to access the customer site.<br/><br/>
    <c:if test="${loginFailed}">
        <b>The username and password you entered are not correct. Please try
            again.</b><br/><br/>
    </c:if>
    <form:form method="post" modelAttribute="loginForm">
        <form:label path="username">Username</form:label><br/>
        <form:input path="username"/><br/><br/>
        <form:label path="password">Password</form:label><br/>
        <form:password path="password"/><br/><br/>
        <input type="submit" value="Log In">
    </form:form>
</template:loggedOut>