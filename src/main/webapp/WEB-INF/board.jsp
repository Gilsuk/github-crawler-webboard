<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
	integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh"
	crossorigin="anonymous">
<title>repos</title>
</head>
<body>
<div class="container">
<table class="table table-bordered table-sm">
	<thead class="thead-dark">
		<tr>
			<th scope="col">제목</th>
			<th scope="col">툴</th>
			<th scope="col">조회수</th>
			<th scope="col">소개1줄</th>
			<th scope="col">키워드</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${list }" var="item">
		<tr>
			<th scope="row">${item.title }</th>
			<td>${item.langs }</td>
			<td>${item.star }</td>
			<td>${item.description }</td>
			<td>${item.tags }</td>
		</tr>
		</c:forEach>
	</tbody>
</table>
<nav aria-label="Page navigation example">
	<ul class="pagination pagination-sm justify-content-center" >

	<!-- 이전 페이지로 가기 -->
	<c:if test="${paging.curPage gt 1}">
	<li class="page-item"><a class="page-link" href="/board?curPage=${paging.curPage - 1 }">Prev</a></li>
	</c:if>
	
	<!-- 페이징 리스트 -->
	<c:forEach begin="${paging.startPage}" end="${paging.endPage }" var="i">
		<c:choose>
			<c:when test="${paging.curPage eq i }">
				<li class="page-item active" aria-current="page"><a class="page-link" href="/board?curPage=${i }">${i}</a></li>
			</c:when>
			<c:otherwise>
				<li class="page-item"><a class="page-link" href="/board?curPage=${i }">${i}</a></li>
			</c:otherwise>
		</c:choose>
	</c:forEach>

	<!-- 다음 페이지로 가기 -->
	<c:if test="${paging.curPage ne paging.totalPage}">
	<li class="page-item"><a class="page-link" href="/board?curPage=${paging.curPage + 1 }">Next</a></li>
	</c:if>
	
	</ul>
</nav>
<form method="get" action="/board">
	<div class="form-row align-items-center">
		<div class="col-1 offset-4 my-1">
			<select class="custom-select mr-sm-2" id="inlineFormCustomSelect"
				name="category">
				<option value="title" selected="selected">제목</option>
				<option value="desc">소개</option>
				<option value="lang">툴</option>
				<option value="tag">키워드</option>
			</select>
		</div>
		<div class="col-2 my-1">
			<input class="form-control" name="keyword" type="text" required="required" placeholder="키워드 입력" />
		</div>
		<div class="col-1 my-1">
			<button type="submit" class="btn btn-primary">검색</button>
		</div>
	</div>
</form>
</div>
</body>
</html>