<%-- 
    Document   : Stat
    Created on : Apr 12, 2019, 9:18:56 PM
    Author     : antil
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Service Statistics</title>
    </head>
    <body>
        <h1>Let's look at the operational analytics of DotA2 match history service</h1>
        <p>The average kills of all users: <%=request.getAttribute("killAvg")%></p>
        <p>The average deaths of all users: <%=request.getAttribute("deathAvg")%></p>
        <p>The most popular hero used by all users: <%=request.getAttribute("pHero")%></p>
        <br></br>
        <br></br>
        <p>Last 5 times of user log in:</p>
        <br></br>
        <p><%=request.getAttribute("user1")%></p>
        <br></br>
        <p><%=request.getAttribute("user2")%></p>
        <br></br>
        <p><%=request.getAttribute("user3")%></p>
        <br></br>
        <p><%=request.getAttribute("user4")%></p>
        <br></br>
        <p><%=request.getAttribute("user5")%></p>
    </body>
</html>
