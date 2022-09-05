# 톰캣 구현하기

## 기능 요구사항

### 1단계 
- [X] GET /index.html 응답하기
  - 요청 url에 맞는 view 반환
- [x] html, jc, css 지원하기
  - 확장자에 맞게 content-type을 지정하여 응답하도록 처리
- [x] Query String 파싱
  - 로그인 처리를 위한 사용자 정보 query param 매핑


### 2단계
- HTTP Status Code 302
  - [x] 로그인에 성공하면 응답 헤더에 http status code를 302로 반환하고 /index.html로 리다이렉트
  - [x] 로그인에 실패하면 /401.html로 리다이렉트

- POST 방식으로 회원가입
  - [ ] http://localhost:8080/register으로 접속하면 회원가입 페이지(register.html)
    - 회원가입 페이지를 보여줄 때는 GET
  - [ ] 회원가입을 완료하면 index.html로 리다이렉트

- [ ] 로그인 페이지도 버튼을 눌렀을 때 GET 방식에서 POST 방식으로 전송하도록 변경

- Cookie에 JSESSIONID 값 저장
  - Cookie 클래스를 추가 
  - [ ] HTTP Request Header의 Cookie에 JSESSIONID가 없으면 HTTP Response Header에 Set-Cookie를 반환

- Session 구현
  - [ ] 쿠키에서 전달 받은 JSESSIONID의 값으로 로그인 여부 체크
  - [ ] 로그인에 성공하면 Session 객체의 값으로 User 객체를 저장

- [ ] 로그인된 상태에서 /login 페이지에 HTTP GET method로 접근하면 이미 로그인한 상태니 index.html 페이지로 리다이렉트