<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Error - An Error Occurred</title>
<style>
    body {
        font-family: 'Arial', sans-serif;
        background-color: #f4f4f4;
        color: #333;
        text-align: center;
        padding: 50px;
    }

    h1 {
        font-size: 3em;
        color: #e74c3c;
    }

    p {
        font-size: 1.5em;
        color: #555;
    }

    .container {
        max-width: 600px;
        margin: 0 auto;
        padding: 20px;
        background-color: #fff;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        border-radius: 8px;
    }

    .button {
        display: inline-block;
        margin-top: 20px;
        padding: 10px 20px;
        font-size: 1.2em;
        background-color: #3498db;
        color: white;
        text-decoration: none;
        border-radius: 5px;
    }

    .button:hover {
        background-color: #2980b9;
    }
</style>
</head>
<body>
    <div class="container">
        <h1>An Error Occurred</h1>
        <p>Sorry, something went wrong. Please try again later.</p>
        <c:if test="${not empty errors}">
            <ul style="text-align: left; color: #e74c3c; list-style: none; padding: 0;">
                <c:forEach items="${errors.fieldErrors}" var="err">
                    <li>${err.field}: ${err.defaultMessage}</li>
                </c:forEach>
            </ul>
        </c:if>
        <a href="/" class="button">Return to Home</a>
    </div>
</body>
</html>
