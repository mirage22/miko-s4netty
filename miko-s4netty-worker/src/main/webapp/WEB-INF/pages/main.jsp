<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Spring 4 Netty - Worker</title>
</head>
    <body>
        <h1>STATUS : ${message}</h1>
        <h3>JSON Service Response powered by Netty</h3>
        <h4>port: ${tcpPort}</h4>
        <table border="1" >

            <tr>
                <td>Make</td>
                <td>Model</td>
                <td>Year</td>
            </tr>

            <c:forEach items="${displayCars}" var="car">
                <tr>
                    <td><c:out value="${car.make}"/></td>
                    <td><c:out value="${car.model}"/></td>
                    <td><c:out value="${car.year}"/></td>
                </tr>
            </c:forEach>

        </table>
    </body>
</html>