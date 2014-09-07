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
	</div>
	
	<div id="navigation">

	</div>

	<div id="content">
		<xsl:for-each select="test_cases">
			<table>
			<thead>
			<tr> 
			<th>Test cases</th>
			<th class="time"><xsl:value-of select="@elapsed_time"/></th>
			<th class="counter"><xsl:value-of select="count(.//test_case//test_result[@result='OK'])"/></th>
			<th class="counter"><xsl:value-of select="count(.//test_case//test_result[@result='NOK'])"/></th>
			</tr> 
			</thead>
			<tbody> 
			<xsl:for-each select="test_case">
			<tr>
				<xsl:choose>
					<xsl:when test="@result = 'OK'">
						<td style="background-color:#ACFA58;"><a><xsl:attribute name="href"><xsl:value-of select="@test_source"/></xsl:attribute><xsl:value-of select="@name"/></a></td>
					</xsl:when>
					<xsl:when test="@result = 'NOK'">
						<td style="background-color:#FF5F5F;"><a><xsl:attribute name="href"><xsl:value-of select="@test_source"/></xsl:attribute><xsl:value-of select="@name"/></a></td>
					</xsl:when>
					<xsl:otherwise>
						<td style="background-color:grey;"><a><xsl:attribute name="href"><xsl:value-of select="@test_source"/></xsl:attribute><xsl:value-of select="@name"/></a></td>
					</xsl:otherwise>
				</xsl:choose>
				<td class="time"><xsl:value-of select="@elapsed_time"/></td>
				<td class="counter"><xsl:value-of select="count(.//test_result[@result='OK'])"/></td>
				<td class="counter"><xsl:value-of select="count(.//test_result[@result='NOK'])"/></td>
			</tr>
			</xsl:for-each>
			</tbody>
			</table>
		</xsl:for-each>
	</div>
	
</div>
</body>
</html>
</xsl:template>
</xsl:stylesheet>