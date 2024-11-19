<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="CumbresMalvinas" tagdir="/WEB-INF/tags" %>

<CumbresMalvinas:layout pageName="owners">
    <h2>
        <c:if test="${owner['new']}">New </c:if> Owner
    </h2>
    <form:form modelAttribute="owner" class="form-horizontal" id="add-owner-form">
        <div class="form-group has-feedback">
            <CumbresMalvinas:inputField label="First Name" name="firstName"/>
            <CumbresMalvinas:inputField label="Last Name" name="lastName"/>
            <CumbresMalvinas:inputField label="Address" name="address"/>
            <CumbresMalvinas:inputField label="City" name="city"/>
            <CumbresMalvinas:inputField label="Telephone" name="telephone"/>
            <CumbresMalvinas:inputField label="Username" name="user.username"/>
            <CumbresMalvinas:inputField label="Password" name="user.password"/>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
                <c:choose>
                    <c:when test="${owner['new']}">
                        <button class="btn btn-default" type="submit">Add Owner</button>
                    </c:when>
                    <c:otherwise>
                        <button class="btn btn-default" type="submit">Update Owner</button>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </form:form>
</CumbresMalvinas:layout>
