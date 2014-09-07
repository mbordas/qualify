<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright (c) 2010-2012, Mathieu Bordas
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

 
<xsl:template match="source">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title><xsl:value-of select="@test_case_file"/></title>
<link rel="stylesheet" media="screen" type="text/css" href="../style/qualify.css" />
<script type="text/javascript" src="../style/jquery.js"></script>
<script type="text/javascript" src="../style/jquery.tools.min.js"></script>
<script type="text/javascript"> 
	<![CDATA[
	
	function renderGraphic(graphic) {
		var ctx = graphic.getContext('2d');
		ctx.font="10pt Verdana,Helvetica,Arial,sans-serif"
		var img = new Image();
		img.src = $(graphic).attr('background_image');
		
		img.onload = function() {
			ctx.drawImage(img, 0, 0);
			var oX = $(graphic).attr('x')
			var oY = $(graphic).attr('y')
			if($(graphic).attr('show_areas') == "true") {
				$(graphic).children("area").each( function() {
					ctx.strokeStyle = $(this).attr('color');
					ctx.strokeRect($(this).attr('x') - oX, $(this).attr('y') - oY, $(this).attr('width'), $(this).attr('height'));
					var label = $(this).attr('label')
					textWidth = ctx.measureText(label).width;
					ctx.fillStyle = "white";
					ctx.fillRect($(this).attr('x') - oX, $(this).attr('y') - oY - 15, textWidth, 14);
					ctx.fillStyle = $(this).attr('color');
					ctx.fillText(label, $(this).attr('x') - oX, $(this).attr('y') - oY - 2);
				});
				$(graphic).children("line").each( function() {
					ctx.strokeStyle = $(this).attr('color');
					ctx.moveTo($(this).attr('x1') - oX, $(this).attr('y1') - oY);
    				ctx.lineTo($(this).attr('x2') - oX, $(this).attr('y2') - oY);
    				ctx.stroke();
				});
				$(graphic).children("point").each( function() {
					ctx.strokeStyle = $(this).attr('color');
					var x = $(this).attr('x') - oX
					var y = $(this).attr('y') - oY
					ctx.moveTo(x, y - 5);
    				ctx.lineTo(x, y + 5);
    				ctx.moveTo(x - 5, y);
    				ctx.lineTo(x + 5, y);
    				ctx.stroke();
    				var label = $(this).attr('label')
					textWidth = ctx.measureText(label).width;
    				ctx.fillStyle = "white";
					ctx.fillRect(x, y - 15, textWidth, 14);
					ctx.fillStyle = $(this).attr('color');
					ctx.fillText(label, x, y - 2);
				});
			}
		}
	}

	$(".graphic").each( function() {
		renderGraphic($(this).get(0));
		$(this).get(0).onclick = function () {
			if($(this).attr('show_areas') == "true") {
				$(this).attr('show_areas', 'false')
			} else {
				$(this).attr('show_areas', 'true')
			}
			renderGraphic($(this).get(0));
		};
	});
	
]]>
</script>

</head>
<body>
	<div class="page">
	<table>
		<tr><th colspan="2">Summary</th></tr>
		<tr><td>Test case name</td><td><xsl:value-of select="@test_case_file"/></td></tr>
		<tr><td>Global result</td><td><xsl:value-of select="@test_case_result"/></td></tr>
		<tr><td>Number of OKs</td><td><xsl:value-of select="@number_of_oks"/></td></tr>
		<tr><td>Number of NOKs</td><td><xsl:value-of select="@number_of_noks"/></td></tr>
		<xsl:for-each select="exception">
		<tr class="nok_bg"><td><xsl:value-of select="@label"/></td><td><xsl:value-of select="text()"/></td></tr>
		</xsl:for-each>
	</table>
	<table class="code">
	<tr><th colspan="2">Steps</th></tr>
	
	<script>
$(function() {
	$(".tabs").tabs(".panes > div");
});
</script>

	<!-- TABS -->
	<ul class="tabs">
	<xsl:for-each select="step">
		<xsl:choose>
			<xsl:when test="count(*[@type='result_nok'])>=1">
				<span class="steptitle nok"><a href="#"><xsl:value-of select="@title"/></a></span>
			</xsl:when>
			<xsl:when test="count(*[@type='result_ok'])>=1">
				<span class="steptitle ok"><a href="#"><xsl:value-of select="@title"/></a></span>
			</xsl:when>
			<xsl:otherwise>
				<span class="steptitle"><a href="#"><xsl:value-of select="@title"/></a></span>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:for-each>
	</ul>
	
	<!-- STEPS -->
	<div class="panes">
		<xsl:for-each select="step">
			<div>
			<table class="code">
		<tr><th colspan="2"><xsl:value-of select="@title"/></th></tr>
		<xsl:for-each select="source_line">
				<tr>
					<td class="line_number"><xsl:value-of select="@line_number"/></td>
					<td class="source_line"><xsl:attribute name="style">
					<xsl:choose>
						<xsl:when test="@type='result_ok'">
						<xsl:text>background-color:#ACFA58;</xsl:text>
						</xsl:when>
						<xsl:when test="@type='result_nok'">
						<xsl:text>background-color:#FF5F5F;</xsl:text>
						</xsl:when>
					</xsl:choose>
					<xsl:value-of select=".//@padding-left"/></xsl:attribute>
					<xsl:choose>
						<xsl:when test="code">
							<xsl:value-of select="code/text()"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates/>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:if test="@requirement">
					<span class="mark"><xsl:value-of select="@requirement"/></span>
					</xsl:if>
					</td>
				</tr>

				<xsl:if test="code">
					<xsl:for-each select="test_result">
						<tr>
							<td class="line_number"></td>
							<td class="source_line"><xsl:attribute name="style"><xsl:text>background-color:#E6E6E6;</xsl:text><xsl:value-of select="..//@padding-left"/></xsl:attribute>
							<xsl:apply-templates/>
							</td>
						</tr>
					</xsl:for-each>
					<xsl:for-each select="comment">
						<tr>
							<td class="line_number"></td>
							<td class="source_line"><xsl:attribute name="style"><xsl:text>background-color:#E6E6E6;</xsl:text><xsl:value-of select="..//@padding-left"/></xsl:attribute>
							<xsl:apply-templates/>
							</td>
						</tr>
					</xsl:for-each>
					<xsl:for-each select="attachment">
						<tr>
						<td class="line_number"></td>
						<td class="source_line"><xsl:attribute name="style"><xsl:text>background-color:#E6E6E6;</xsl:text><xsl:value-of select="..//@padding-left"/></xsl:attribute>
						<xsl:choose>
						<xsl:when test="@type='INLINE_IMAGE'">
						<img><xsl:attribute name="src"><xsl:value-of select="@attachment_file"/></xsl:attribute></img>
						</xsl:when>
						<xsl:otherwise>
						<i>Attachment : </i><a><xsl:attribute name="href"><xsl:value-of select="@attachment_file"/></xsl:attribute><xsl:value-of select="@source_file"/></a>
						</xsl:otherwise>
						</xsl:choose>
						</td>
						</tr>
					</xsl:for-each>
				</xsl:if>
		</xsl:for-each>
		</table>
		</div>
		</xsl:for-each>
	</div>
	</table>
	</div>
</body>
</html>
</xsl:template>

<xsl:template match="span">
  <span>
  <xsl:if test="@css_class">
    <xsl:attribute name="class"><xsl:value-of select="@css_class"/></xsl:attribute>
  </xsl:if>
  <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match="string">
  <xsl:text/>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="field">
  <span class="label"><xsl:value-of select="@label"/></span><span><xsl:apply-templates/></span>
</xsl:template>

<xsl:template match="table">
  <table>
  <xsl:apply-templates/>
  </table>
</xsl:template>

<xsl:template match="entry">
  <tr>
  <xsl:apply-templates/>
  </tr>
</xsl:template>

<xsl:template match="cell">
  <td>
  <xsl:apply-templates/>
  </td>
</xsl:template>

<xsl:template match="list">
  <table style="border-style:none;">
  <xsl:apply-templates select="item"/>
  </table>
</xsl:template>

<xsl:template match="item">
  <tr><td><xsl:apply-templates/></td></tr>
</xsl:template>

<xsl:template match="graphic">
	<tr><td>
	<canvas class="graphic" show_areas="true">
	<xsl:choose>
	<xsl:when test="@background_image != ''">
	<xsl:attribute name="background_image"><xsl:value-of select="@background_image"/></xsl:attribute>
	</xsl:when>
	<xsl:otherwise>
	<xsl:attribute name="background_image">./style/undefined_background_image.PNG</xsl:attribute>
	</xsl:otherwise>
	</xsl:choose>
	<xsl:attribute name="x"><xsl:value-of select="@x"/></xsl:attribute>
	<xsl:attribute name="y"><xsl:value-of select="@y"/></xsl:attribute>
	<xsl:attribute name="width"><xsl:value-of select="@width"/></xsl:attribute>
	<xsl:attribute name="height"><xsl:value-of select="@height"/></xsl:attribute>
	<xsl:apply-templates/>
	<xsl:copy-of select="./*" />
	Your browser does not support HTML5.
	</canvas>
	</td></tr>
</xsl:template>

<xsl:template match="tree_node">
  <xsl:if test="@id">
  	<table class="tree_node">
    	<thead>
		<tr>
		<th>tree node: <xsl:value-of select="@id"/></th>
		</tr> 
		</thead>
		<tbody>
			<xsl:choose>
			<xsl:when test="@path_index != ''">
			<xsl:attribute name="id">node-<xsl:value-of select="@path_index"/>-<xsl:value-of select="@local_index"/></xsl:attribute>
			<xsl:attribute name="class">child-of-node-<xsl:value-of select="@path_index"/></xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
			<xsl:attribute name="id">node-<xsl:value-of select="@local_index"/></xsl:attribute>
			</xsl:otherwise>
			</xsl:choose>
		</tbody>
    </table>
  </xsl:if>
</xsl:template>

</xsl:stylesheet>