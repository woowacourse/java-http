# 기능 요구 사항

## 1단계 - HTTP 서버 구현하기
- [x] 1\. GET `index.html` 응답하기
- [x] 2\. CSS 지원하기
- [x] 3\. Query String 파싱

## 2단계 - 로그인 구현하기
- [x] 1\. HTTP Status Code 302
  - [x] 로그인에 성공하면 응답 헤더에 http status code를 302로 반환하고 /index.html로 리다이렉트 한다. 
  - [x] 로그인에 실패하면 401.html로 리다이렉트한다. 
- [ ] 2\. POST 방식으로 회원가입
  - [ ] `http://localhost:8080/register` 으로 접속하면 회원가입 페이지(register.html)를 보여준다.
  - [ ] 회원가입을 완료하면 index.html로 리다이렉트한다.
- [ ] 3\. Cookie에 JSESSIONID 값 저장하기
  - [ ] Cookie 클래스를 추가한다.  
  - [ ] HTTP Request Header의 Cookie에 JSESSIONID가 없으면 HTTP Response Header에 Set-Cookie를 반환해주는 기능을 구현한다.
