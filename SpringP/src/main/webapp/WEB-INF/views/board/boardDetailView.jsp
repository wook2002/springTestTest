<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
	#contentArea{width:100%}
	#contentArea *{margin:5px}
</style>
</head>
<body>
    <jsp:include page="../common/menubar.jsp"/>

    <div class="content">
        <br><br>
        <div class="innerOuter">
            <h2>게시글 상세보기</h2>
            <br>
            
            <br><br>
            <table id="contentArea" align="center" class="table">
                <tr>
                    <th width="100">제목</th>
                    <td colspan="3">${ b.boardTitle }</td>
                </tr>
                <tr>
                    <th>작성자</th>
                    <td>${ b.boardWriter }</td>
                    <th>작성일</th>
                    <td>${ b.createDate }</td>
                </tr>
                <tr>
                    <th>첨부파일</th>
                    <td colspan="3">
                    	<c:if test="${ !empty b.originName }">
                        	<a href="${ pageContext.servletContext.contextPath }/resources/upload_files/${b.changeName}" download="${ b.originName }">${ b.originName }</a>
                        </c:if>
                        <c:if test="${ empty b.originName }">
                        	첨부파일이 없습니다.
                        </c:if>
                    </td>
                </tr>
                <tr>
                    <th>내용</th>
                    <td colspan="3"></td>
                </tr>
                <tr>
                    <td colspan="4"><p style="height:150px">${ b.boardContent }</p></td>
                </tr>
            </table>
            <br>
			<!-- 수정하기, 삭제하기 버튼에 onclick 이벤트 -->
			<c:if test="${ loginUser.userId eq b.boardWriter }">
	            <div align="center">
	                <button class="btn btn-primary" onclick="postFormSubmit(1);">수정하기</button>
	                <button class="btn btn-danger" onclick="postFormSubmit(2);">삭제하기</button>
	            </div>
	            <!-- postFormSubmit으로 bno, fileName이 전달된다. -->
	            <form id="postForm" action="" method="post">
					<input type="hidden" name="bno" value="${ b.boardNo }">
					<input type="hidden" name="fileName" value="${ b.changeName }"> 
				</form>
				<script>
					function postFormSubmit(num){
						var postForm = $("#postForm");
						
						if(num == 1){
							postForm.attr("action", "updateFormBoard.do");
						}else{
							postForm.attr("action", "deleteBoard.do");
						}
						postForm.submit();
					}
				</script>
            </c:if>
            <br><br>

            <table id="replyArea" class="table" align="center">
                <thead>
                    <tr>
                    	<c:if test="${ !empty loginUser }">
	                        <th colspan="2" style="width:75%">
	                            <textarea class="form-control" id="replyContent" rows="2" style="resize:none; width:100%"></textarea>
	                        </th>
	                        <th style="vertical-align: middle"><button class="btn btn-secondary" id="addReply">등록하기</button></th>
                        </c:if>
                        <c:if test="${ empty loginUser }">
                        	<th colspan="2" style="width:75%">
	                            <textarea class="form-control" readonly rows="2" style="resize:none; width:100%">로그인한 사용자만 사용가능한 서비스입니다. 로그인 후 이용해주세요.</textarea>
	                        </th>
	                        <th style="vertical-align: middle"><button class="btn btn-secondary" disabled>등록하기</button></th>
                        </c:if>
                    </tr>
                    <tr>
                       <td colspan="3">댓글 (<span id="rcount">0</span>) </td> 
                    </tr>
                </thead>
                <tbody>
                
                </tbody>
            </table>
        </div>
        <br><br>
    </div>
   
   <script>
 	$(function(){
		selectReplyList(); //조회
		
		$("#addReply").click(function(){ //등록하기 버튼을 누르면 실행되는 함수
    		var bno = ${b.boardNo}; //boardNo를 bno에 담아준다.

			if($("#replyContent").val().trim().length != 0){ //replyContent안에 공백을 제거하고 길이가 0이 아니라면 -> 댓글 내용이 있다면
				
				$.ajax({
					url:"rinsertBoard.do",
					type:"post",
					data:{replyContent:$("#replyContent").val(), //Reply 필드 값이랑 이름이 똑같다.
						  refBoardNo:bno,
						  replyWriter:"${loginUser.userId}"},
					success:function(result){ //통신 성공 시 //json이 아니다. -> insert여서 그냥 result 값을 받아주고 등록하낟.
						if(result > 0){ //댓글 등록 성공시
							$("#replyContent").val(""); //댓글 내용 적는 textarea를 빈 곳으로 만들고
							selectReplyList(); //댓글 list를 조회한다.
							
						}else{ //댓글 등록 실패 시
							alert("댓글등록실패");
						}
					},error:function(){ //통신 실패 시
						console.log("댓글 작성 ajax 통신 실패");
					}
				});
				
			}else{
				alert("댓글등록하셈");
			}
			
		});
	});
 	function selectReplyList(){
		var bno = ${b.boardNo}; //boardNo를 bno 변수에 담아준다.
		$.ajax({
			url:"rlistBoard.do",
			data:{bno:bno}, //bno전달
			type:"get",
			success:function(list){ //성공한 경우
				$("#rcount").text(list.length); //댓글 개수를 rcount라는 id를 가진 span 태그에 넣어준다.
				
				var value="";
				$.each(list, function(i, obj){
					
					if("${loginUser.userId}" == obj.replyWriter){ //댓글 작성자와 현재 로그인한 userId가 같으면
						value += "<tr style='background:#EAFAF1'>";
					}else{
						value += "<tr>";
					}
					
					value += "<th>" + obj.replyWriter + "</th>" + 
								 "<td>" + obj.replyContent + "</td>" + 
								 "<td>" + obj.createDate + "</td>" +
						 "</tr>";
				});
				$("#replyArea tbody").html(value);
			},error:function(){ //실패한 경우
				console.log("댓글 리스트조회용 ajax 통신 실패");
			}
		});
	}   
    </script>

   
    <jsp:include page="../common/footer.jsp"/>
</body>
</html>