<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%--@elvariable id="loginFailed" type="java.lang.Boolean"--%>
<%--@elvariable id="loginForm" type="com.web.site.AuthenticationFiler.Form"--%>
<%--@elvariable id="validationErrors" type="java.util.Set<javax.validation.constraintvalidation>"--%>
<spring:message code="title.login" var="loginTitle" />
<template:loggedOut htmlTitle="${loginTitle}" bodyTitle="${loginTitle}">
    <spring:message code="message.login.instruction" /><br /><br />
    <c:if test="${loginFailed}">
        <b class="error"><spring:message code="error.login.failed" /></b><br /><br />
    </c:if>
    <c:if test="${validationErrors != null}">
        <ul>
            <c:forEach items="${validationErrors}" var="error">
                <li><c:out value="${error.message}"/></li>
            </c:forEach>
        </ul>
    </c:if>
    <form:form method="post" modelAttribute="loginForm">
        <form:label path="username"><spring:message code="field.login.username" /></form:label><br />
        <form:input path="username" /><br /><br />
        <form:errors path="username" cssClass="errors"/><br/>
        <form:label path="password"><spring:message code="field.login.password" /></form:label><br />
        <form:password path="password" /><br /><br />
        <form:errors path="password" cssClass="errors"/><br/>
        <input type="submit" value="<spring:message code="field.login.submit" />" />
    </form:form>
</template:loggedOut>
