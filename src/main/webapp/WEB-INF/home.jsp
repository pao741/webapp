<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<body>
<h2>Welcome, ${username}</h2> <br/>
<%--<a href="/logout"> Logout </a><br/>--%>
<p>${adding_error}</p>
<form method="post">
    <input type="text" name="adding_username" placeholder="username" required /><br>
    <input type="password" name="adding_password" placeholder="password" required/><br>
    <input type="password" name="confirm_password" placeholder="confirm password" required>
    <br>
    <input type="submit" name="add_user" value="Add user" required/>

</form>
<p>${removing_error}</p>
    <table border="2">
        <tr><td>Username List</td></tr>
        <c:forEach items="${userList}" var="usr">
            <tbody style="vertical-align: center">
            <tr>
                <td >
                        ${usr}
                    <td>
                            <form method="post">
                            <c:choose>
                                <c:when test="${usr!=username}">
                                    <input type="hidden" name="user_to_remove" value="${usr}"/>
                                    <input type="submit" name="removing_user" value="remove" onclick="{return confirm('Are you sure you want to remove this user?')}"/>
                                </c:when>
                                <c:otherwise> Removing not allowed here </c:otherwise>
                            </c:choose>
                            </form>
                    </td>
                </td>
<%--                <td>--%>

<%--                </td>--%>
            </tr>
            </tbody>
        </c:forEach>
    </table>
</body>
</html>
