<%--
  ~ Copyright (c) 2006, JetBrains, s.r.o. All Rights Reserved.
  --%>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<props:viewWorkingDirectory />

<div class="parameter">
  Kotlin script: <props:displayValue name="script.content" emptyValue="<empty>" showInPopup="true" popupTitle="Script content" popupLinkText="view script content"/>
</div>
