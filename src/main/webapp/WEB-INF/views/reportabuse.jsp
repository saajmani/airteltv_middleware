<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<style>
.fixed_headers {
  width: 100%;
  table-layout: fixed;
  border-collapse: collapse;
}
.fixed_headers th {
  text-decoration: underline;
}
.fixed_headers th,
.fixed_headers td {
  padding: 5px;
  text-align: left;
}
.fixed_headers td:nth-child(1),
.fixed_headers th:nth-child(1) {
  min-width: 5%;
}
.fixed_headers td:nth-child(2),
.fixed_headers th:nth-child(2) {
  min-width: 200px;
}
.fixed_headers td:nth-child(3),
.fixed_headers th:nth-child(3) {
  width: 300px;
}
.fixed_headers td:nth-child(4),
.fixed_headers th:nth-child(4) {
  width: 250px;
}
.fixed_headers td:nth-child(5),
.fixed_headers th:nth-child(5) {
  width: 350px;
}
.fixed_headers td:nth-child(5),
.fixed_headers th:nth-child(5) {
  width: 300px;
}
.fixed_headers td:nth-child(6),
.fixed_headers th:nth-child(6) {
  width: 400px;
}
.fixed_headers thead {
  background-color: #333;
  color: #FDFDFD;
}
.fixed_headers thead tr {
  display: block;
  position: relative;
}
.fixed_headers tbody {
  display: block;
  overflow: auto;
  width: 100%;
  height: 40em;
}
.fixed_headers tbody tr:nth-child(even) {
  background-color: #DDD;
}
.old_ie_wrapper {
  height: 300px;
  width: 100%;
  overflow-x: hidden;
  overflow-y: auto;
}
.old_ie_wrapper  {
  height: auto;
}

</style>
<title>${message}</title>

</head>
<body>
 <h2>${message}</h2>
<div class="old_ie_wrapper">
<!-- IE < 10 does not like giving a tbody a height.  The workaround here applies the scrolling to a wrapped <div>. -->
<!--[if lte IE 9]>
<!--<![endif]-->

<table class="fixed_headers">
	  <thead>
			 <tr>
				<th>Sl No</th>
				<th>User Id</th>
				<th>Content Id</th>
				<th>CP Id</th>
				<th>Created</th>
				<th>Message</th>
			</tr>
	  </thead>
  <tbody>
    <c:forEach items="${reportabuses}" var="reportabuse"
					varStatus="outer">
					<tr>
						<td><c:out value="${outer.index+1}" /></td>
						<td><c:out value="${reportabuse.userId}" /></td>
						<td ><c:out value="${reportabuse.contentId}" /></td>
						<td><c:out value="${reportabuse.cpId}" /></td>
						<td ><c:out value="${reportabuse.created}" /></td>
						<td><c:out value="${reportabuse.message}" /></td>
					</tr>
				</c:forEach>
  </tbody>
</table>

<!--[if lte IE 9]>

<!--<![endif]-->
</div>
</body>
</html>