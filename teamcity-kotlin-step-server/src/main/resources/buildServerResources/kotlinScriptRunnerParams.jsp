<%@ page import="jetbrains.buildServer.runner.SimpleRunnerConstants" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="admin" tagdir="/WEB-INF/tags/admin" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<props:workingDirectory />

<jsp:include page="/tools/editToolUsage.html?toolType=kotlin&versionParameterName=kotlin.path&class=longField"/>

<tr id="script.content.container" class="customScript">
  <th>
    <label for="script.content">Kotlin script:<l:star/></label>
  </th>
  <td class="codeHighlightTD">
    <props:multilineProperty name="script.content" className="longField" cols="58" rows="10" expanded="true" linkTitle="Enter build script content"
                             note="A Kotlin script code"
                             highlight="shell" />
  </td>
</tr>

<tr class="advancedSetting">
  <th>
    <label for="ktsArgs">Script parameters:</label>
  </th>
  <td>
    <props:textProperty name="ktsArgs" className="longField" expandable="true"/>
  </td>
</tr>

<props:javaSettings/>
