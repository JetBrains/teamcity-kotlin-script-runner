<%@ page import="jetbrains.buildServer.runner.SimpleRunnerConstants" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="admin" tagdir="/WEB-INF/tags/admin" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<jsp:useBean id="propertyNames" class="jetbrains.buildServer.runner.kotlinBuildStep.KotlinScriptPropertyNamesProvider"/>

<props:workingDirectory />

<jsp:include page="/tools/editToolUsage.html?toolType=kotlin.compiler&versionParameterName=${propertyNames.kotlinPath}&class=longField"/>

<props:selectSectionProperty name="${propertyNames.scriptType}" title="Script type:">

  <props:selectSectionPropertyContent value="${propertyNames.typeCustom}" caption="Custom Script">
    <tr id="script.content.container">
      <th>
        <label for="${propertyNames.scriptContent}">Kotlin script:<l:star/></label>
      </th>
      <td class="codeHighlightTD">
        <props:multilineProperty name="${propertyNames.scriptContent}" className="longField" cols="58" rows="10" expanded="true" linkTitle="Enter build script content"
                                 note="A Kotlin script code"
                                 highlight="shell" />
      </td>
    </tr>
  </props:selectSectionPropertyContent>
  <props:selectSectionPropertyContent value="${propertyNames.typeFile}" caption="Script File">
    <tr id="script.content.container">
      <th>
        <label for="${propertyNames.scriptFile}">Kotlin script file:<l:star/></label>
      </th>
      <td class="codeHighlightTD">
        <props:textProperty name="${propertyNames.scriptFile}" className="longField">
          <jsp:attribute name="afterTextField">
            <bs:vcsTree fieldId="${propertyNames.scriptFile}"/>
          </jsp:attribute>
        </props:textProperty>
        <span id="error_${propertyNames.scriptFile}" class="error"></span>
        <span class="smallNote">To support annotation-based references to Maven dependencies, the provided file must have the <i>*.main.kts</i> extension. <bs:help file="kotlin-script-file"/></span>
      </td>
    </tr>
  </props:selectSectionPropertyContent>
</props:selectSectionProperty>


<tr class="advancedSetting">
  <th>
    <label for="${propertyNames.kotlinArgs}">Script parameters:</label>
  </th>
  <td>
    <props:textProperty name="${propertyNames.kotlinArgs}" className="longField" expandable="true"/>
  </td>
</tr>

<props:javaSettings/>
