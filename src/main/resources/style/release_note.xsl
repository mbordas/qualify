<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright (c) 2010, Mathieu Bordas
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
2- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
3- Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" encoding="utf-8" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" doctype-system=
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"/>
<xsl:template match="/">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>Qualify - Release Note</title>
<link rel="stylesheet" media="screen" type="text/css" href="style/qualify.css" />

<link href="style/jquery.treeTable.css" rel="stylesheet" type="text/css" />
  <script type="text/javascript" src="style/jquery.js"></script>
  <script type="text/javascript" src="style/jquery.ui.js"></script> 
  <script type="text/javascript" src="style/jquery.treeTable.js"></script>
  <script type="text/javascript"> 
<![CDATA[
  $(document).ready(function() {
    $("#requirements").treeTable();
 });
]]>
  </script>
</head>
<body>
<div id="global">

	<div id="header">
		<xsl:for-each select="release_note">
		<h1>
			<img alt="" src="style/qualify_short_logo.png" />
			<xsl:value-of select="@sut_name"/>
		</h1>
		<p class="sous-titre">
			<strong>Version: </strong><xsl:value-of select="@version"/>
			(<xsl:value-of select="@date"/>)
		</p>
		
		<table>
		<tr><th colspan="2">Summary</th></tr>
		<tr><td>Requirements</td><td><xsl:value-of select="@successful_requirements_percent"/> %</td></tr>
		<tr><td>Test cases</td><td><xsl:value-of select="@successful_test_cases_percent"/> %</td></tr>
		<tr><td>Checks</td><td><xsl:value-of select="@successful_checks_percent"/> %</td></tr>
		<xsl:if test="count(/release_note/error)!=0">
		<tr><th colspan="2" style="color:#FF5F5F;">Errors</th></tr>
		<xsl:for-each select="error">
		<tr><td colspan="2"><xsl:value-of select="text()"/></td></tr>
		</xsl:for-each>
		</xsl:if>
		<xsl:if test="count(/release_note/warning)!=0">
		<tr><th colspan="2" style="color:#FF8000">Warnings</th></tr>
		<xsl:for-each select="warning">
		<tr><td colspan="2"><xsl:value-of select="text()"/></td></tr>
		</xsl:for-each>
		</xsl:if>
		</table>
		</xsl:for-each>
	</div>
	
	<div id="navigation">

	</div>

	<div id="content">
		<table id="requirements"> 
		<thead> 
		<tr> 
		<th>Requirements</th>
		</tr> 
		</thead> 
		<tbody>
		<xsl:for-each select="release_note//requirement">
			<xsl:if test="@result = 'OK'">
			<tr>
			<xsl:choose>
			<xsl:when test="@path_index != ''">
			<xsl:attribute name="id">node-<xsl:value-of select="@path_index"/>-<xsl:value-of select="@local_index"/></xsl:attribute>
			<xsl:attribute name="class">child-of-node-<xsl:value-of select="@path_index"/></xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
			<xsl:attribute name="id">node-<xsl:value-of select="@local_index"/></xsl:attribute>
			</xsl:otherwise>
			</xsl:choose>
			<td><a class="ok">
			<xsl:attribute name="href">#<xsl:value-of select="@id"/></xsl:attribute>
			<xsl:value-of select="@id"/></a>
			</td>
			</tr>
			</xsl:if>
			<xsl:if test="@result = 'NOK'">
			<tr>
			<xsl:choose>
			<xsl:when test="@path_index != ''">
			<xsl:attribute name="id">node-<xsl:value-of select="@path_index"/>-<xsl:value-of select="@local_index"/></xsl:attribute>
			<xsl:attribute name="class">child-of-node-<xsl:value-of select="@path_index"/></xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
			<xsl:attribute name="id">node-<xsl:value-of select="@local_index"/></xsl:attribute>
			</xsl:otherwise>
			</xsl:choose>
			<td><a class="nok"><xsl:attribute name="href">#<xsl:value-of select="@id"/></xsl:attribute><xsl:value-of select="@id"/></a>
			</td>
			</tr>
			</xsl:if>
		</xsl:for-each>
		</tbody>
		</table>
		<br/>
		<p><a href="test_cases_table.xml">Table of test cases</a>
		</p>
		<br/>
		<xsl:for-each select="release_note//requirement">
		<table>
			<xsl:choose>
				<xsl:when test="@result = 'OK'">
					<th><a><xsl:attribute name="name"><xsl:value-of select="@id"/></xsl:attribute></a><td style="color:#0B610B;"><xsl:value-of select="@id"/></td></th>
					<th class="counter"><xsl:value-of select="count(.//test_result[@result='OK'])"/></th>
					<th class="counter"><xsl:value-of select="count(.//test_result[@result='NOK'])"/></th>
				</xsl:when>
				<xsl:when test="@result = 'NOK'">
					<th><a><xsl:attribute name="name"><xsl:value-of select="@id"/></xsl:attribute></a><td style="color:#FF5F5F;"><xsl:value-of select="@id"/></td></th>
					<th class="counter"><xsl:value-of select="count(.//test_result[@result='OK'])"/></th>
					<th class="counter"><xsl:value-of select="count(.//test_result[@result='NOK'])"/></th>
				</xsl:when>
			</xsl:choose>
			<xsl:for-each select="test_case">
			<tr>
			<xsl:choose>
				<xsl:when test="count(*[@result='NOK'])>=1">
				<xsl:choose>
				<xsl:when test="@test_source">
				<td style="background-color:#FF5F5F;"><a><xsl:attribute name="href"><xsl:value-of select="@test_source"/></xsl:attribute><xsl:value-of select="@name"/></a></td>
				</xsl:when>
				<xsl:when test="@test_case_name">
				<td style="background-color:#FF5F5F;"><xsl:value-of select="@test_case_name"/></td>
				</xsl:when>
				<xsl:otherwise>
				<td style="background-color:#FF5F5F;">Error: attributes 'test_source' and 'test_case_name' not found!</td>
				</xsl:otherwise>
				</xsl:choose>
				<td class="counter"><xsl:value-of select="count(*[@result='OK'])"/></td>
				<td class="counter"><xsl:value-of select="count(*[@result='NOK'])"/></td>
				</xsl:when>
				<xsl:when test="count(*[@result='NOK'])=0">
				<xsl:choose>
				<xsl:when test="@test_source">
				<td style="background-color:#ACFA58;"><a><xsl:attribute name="href"><xsl:value-of select="@test_source"/></xsl:attribute><xsl:value-of select="@name"/></a></td>
				</xsl:when>
				<xsl:when test="@test_case_name">
				<td style="background-color:#ACFA58;"><xsl:value-of select="@test_case_name"/></td>
				</xsl:when>
				<xsl:otherwise>
				<td style="background-color:#ACFA58;">Error: attributes 'test_source' and 'test_case_name' not found!</td>
				</xsl:otherwise>
				</xsl:choose>
				<td class="counter"><xsl:value-of select="count(*[@result='OK'])"/></td>
				<td class="counter"><xsl:value-of select="count(*[@result='NOK'])"/></td>
				</xsl:when>
			</xsl:choose>
			</tr>
			</xsl:for-each>
		</table>
		<br/>
		</xsl:for-each>
	</div>
	
</div>
</body>
</html>
</xsl:template>
</xsl:stylesheet>