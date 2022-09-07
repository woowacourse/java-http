# 톰캣 구현하기

## 1단계: HTTP 서버 구현하기 
- [x] http://localhost:8080/index.html 페이지에 접근 가능하다.
- [x] 접근한 페이지의 js, css 파일을 불러올 수 있다.
- [x] uri의 QueryString을 파싱하는 기능이 있다.

## 2단계: 로그인 구현하기 
- [x] HTTP Reponse의 상태 응답 코드를 302로 반환한다.
- [x] POST로 들어온 요청의 Request Body를 파싱할 수 있다.
- [ ] 로그인에 성공하면 HTTP Reponse의 헤더에 Set-Cookie가 존재한다.
- [ ] 서버에 세션을 관리하는 클래스가 있고, 쿠키로부터 전달 받은 JSESSIONID 값이 저장된다.
