<%@page import="java.sql.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<meta name="viewport"
	content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
<meta http-equiv="X-UA-Compatible" content="ie=edge">
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
	integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh"
	crossorigin="anonymous">

<title>Document</title>
</head>
<body class="bg-light">
	<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
		<div class="container-fluid">
			<a class="navbar-brand" href="#"> <img
				th:src="@{/images/logo.png}" src="../static/images/logo.png"
				width="auto" height="40" class="d-inline-block align-top" alt="" />
			</a>
			<button class="navbar-toggler" type="button" data-toggle="collapse"
				data-target="#navbarSupportedContent"
				aria-controls="navbarSupportedContent" aria-expanded="false"
				aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
			</button>

			<div class="collapse navbar-collapse" id="navbarSupportedContent">
				<ul class="navbar-nav mr-auto"></ul>
				<ul class="navbar-nav">
					<li class="nav-item active"><a class="nav-link" href="/adminhome">Home
							Page</a></li>
					<li class="nav-item active"><a class="nav-link" href="/logout">Logout</a>
					</li>

				</ul>

			</div>
		</div>
	</nav>
	<div class="container-fluid">


		<table class="table" data-testid="user-products-table">

			<tr>
				<th scope="col">Serial No.</th>
				<th scope="col">
					<a data-testid="sort-name" href="?page=0&size=${pageSize}&sort=name,${sortField == 'name' && sortDirection == 'asc' ? 'desc' : 'asc'}">
						Product Name
						<c:if test="${sortField == 'name'}">
							<c:choose><c:when test="${sortDirection == 'asc'}">&#9650;</c:when><c:otherwise>&#9660;</c:otherwise></c:choose>
						</c:if>
					</a>
				</th>
				<th scope="col">Category</th>
				<th scope="col">Preview</th>
				<th scope="col">Quantity</th>
				<th scope="col">
					<a data-testid="sort-price" href="?page=0&size=${pageSize}&sort=price,${sortField == 'price' && sortDirection == 'asc' ? 'desc' : 'asc'}">
						Price
						<c:if test="${sortField == 'price'}">
							<c:choose><c:when test="${sortDirection == 'asc'}">&#9650;</c:when><c:otherwise>&#9660;</c:otherwise></c:choose>
						</c:if>
					</a>
				</th>
				<th scope="col">Weight</th>
				<th scope="col">Descrption</th>
				<th scope="col">Buy</th>

			</tr>
			<tbody>
			<c:forEach var="product" items="${products}">
				<tr>




					<td>
                    						${product.id}
                    					</td>
                    					<td>
                    						${product.name }
                    					</td>
                    					<td>
                    						${product.category.name}

                    					</td>

                    					<td><img src="${product.image}"
                    						height="100px" width="100px"></td>
                    					<td>
                    						${product.quantity }
                    					</td>
                    					<td>S
                    						${product.price }
                    					</td>
                    					<td>
                    						${product.weight }
                    					</td>
                    					<td>
                    						${product.description }
                    					</td>


					<td>


				    <form action="products/addtocart" method="get">
							<input type="hidden" name="id" value="${product.id}">
							<input type="submit" value="Add To Cart" class="btn btn-warning">
					</form>
					</td>


				</tr>
           </c:forEach>

			</tbody>
		</table>

		<c:if test="${totalPages > 0}">
		<div class="d-flex justify-content-between align-items-center mt-3 mb-3" data-testid="pagination-controls">
			<span class="text-muted">Showing ${startItem}&ndash;${endItem} of ${totalElements}</span>
			<nav aria-label="Page navigation">
				<ul class="pagination mb-0">
					<li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
						<a class="page-link" href="?page=0&size=${pageSize}&sort=${sortField},${sortDirection}">First</a>
					</li>
					<li class="page-item ${!hasPrevious ? 'disabled' : ''}">
						<a class="page-link" href="?page=${currentPage - 1}&size=${pageSize}&sort=${sortField},${sortDirection}">Previous</a>
					</li>
					<c:forEach var="i" begin="${pageStart}" end="${pageEnd}">
						<li class="page-item ${i == currentPage ? 'active' : ''}">
							<a class="page-link" href="?page=${i}&size=${pageSize}&sort=${sortField},${sortDirection}">${i + 1}</a>
						</li>
					</c:forEach>
					<li class="page-item ${!hasNext ? 'disabled' : ''}">
						<a class="page-link" href="?page=${currentPage + 1}&size=${pageSize}&sort=${sortField},${sortDirection}">Next</a>
					</li>
					<li class="page-item ${currentPage == totalPages - 1 ? 'disabled' : ''}">
						<a class="page-link" href="?page=${totalPages - 1}&size=${pageSize}&sort=${sortField},${sortDirection}">Last</a>
					</li>
				</ul>
			</nav>
		</div>
		</c:if>

	</div>



	<script src="https://code.jquery.com/jquery-3.4.1.slim.min.js"
		integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n"
		crossorigin="anonymous"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"
		integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo"
		crossorigin="anonymous"></script>
	<script
		src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"
		integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6"
		crossorigin="anonymous"></script>
</body>
</html>