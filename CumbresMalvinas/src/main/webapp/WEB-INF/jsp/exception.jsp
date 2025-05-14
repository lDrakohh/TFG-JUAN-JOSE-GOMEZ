<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="CumbresMalvinas" tagdir="/WEB-INF/tags" %>

<CumbresMalvinas:layout pageName="error">

    <h2>UPS, creo que ha habido un error, inténtalo de nuevo...</h2>

    <c:out value="${exception.message}" />

</CumbresMalvinas:layout>
