# 기능 요구 사항

## 1단계 - HTTP 서버 구현하기
- [x] 1\. GET `index.html` 응답하기
- [x] 2\. CSS 지원하기
- [x] 3\. Query String 파싱

## 2단계 - 로그인 구현하기
- [x] 1\. HTTP Status Code 302
  - [x] 로그인에 성공하면 응답 헤더에 http status code를 302로 반환하고 /index.html로 리다이렉트 한다. 
  - [x] 로그인에 실패하면 401.html로 리다이렉트한다. 
- [x] 2\. POST 방식으로 회원가입
  - [x] `http://localhost:8080/register` 으로 접속하면 회원가입 페이지(register.html)를 보여준다.
  - [x] 회원가입을 완료하면 `/index.html`로 리다이렉트한다.
- [x] 3\. Cookie에 JSESSIONID 값 저장하기
  - [x] Cookie 클래스를 추가한다.  
  - [x] HTTP Request Header의 Cookie에 JSESSIONID가 없으면 HTTP Response Header에 Set-Cookie를 반환해주는 기능을 구현한다.
- [ ] \4. Session 구현하기
  - [ ] 쿠키에서 전달 받은 JSESSIONID의 값으로 로그인 여부를 체크할 수 있어야 한다.
  - [ ] 로그인에 성공하면 Session 객체의 값으로 User 객체를 저장해보자.
  - [ ] 그리고 로그인된 상태에서 /login 페이지에 HTTP GET method로 접근하면 이미 로그인한 상태니 index.html 페이지로 리다이렉트 처리한다.
