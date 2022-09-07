# 톰캣 구현하기

### 공식 문서 및 참고 사이트

- [RFC 2616](https://www.rfc-editor.org/rfc/rfc2616)
- [MDN HTTP](https://developer.mozilla.org/en-US/docs/Web/HTTP)

## 1단계 요구사항

- [x] GET /index.html 응답하기 
  - [x] 인덱스 페이지(http://localhost:8080/index.html)에 접근하도록 구현 
  - [x] Http11ProcessorTest 테스트 클래스의 모든 테스트 통과

- [x] JS, CSS 지원하기
  - [x] Content-Type : text/css
  - [x] Content-Type : text/js
- [x] Query String 파싱
  - [x] http://localhost:8080/login?account=gugu&password=password으로 접속하면 로그인 페이지(login.html) 띄우기
  - [x] 로그인 페이지에 접속했을 때 Query String을 파싱해서 아이디, 비밀번호가 일치하면 회원을 조회한 결과 보여주기

## 2단계 요구사항

- [x] HTTP Reponse의 상태 응답 코드를 302로 반환 
- [x] `/login` 페이지에서 아이디는 gugu, 비밀번호는 password를 입력 후
  - [x] 로그인에 성공하면 응답 헤더에 http status code를 302로 반환하고 `/index.html`로 리다이렉트
  - [x] 로그인에 실패하면 `401.html`로 리다이렉트
- [x] POST로 들어온 요청의 Request Body 파싱
  - [x] http://localhost:8080/register으로 접속하면 회원가입 페이지(register.html) 띄우기 
  - [x] 회원가입 페이지를 보여줄 때는 GET 사용 
  - [x] 회원가입을 버튼을 누르면 HTTP method를 GET이 아닌 POST를 사용
  - [x] 회원가입을 완료하면 `index.html`로 리다이렉트 
  - [x] 로그인 페이지도 버튼을 눌렀을 때 GET 방식에서 POST 방식으로 전송하도록 변경
- [x] 로그인에 성공하면 HTTP Reponse의 헤더에 Set-Cookie 존재
  - [x] 로그인에 성공하면 쿠키와 세션을 활용해서 로그인 상태 유지 
  - [x] HTTP 서버는 세션을 사용해서 서버에 로그인 여부 저장
  - [x] 서버에서 HTTP 응답을 전달할 때 응답 헤더에 Set-Cookie를 추가하고 `JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46` 형태로 값을 전달하면 클라이언트 요청 헤더의 Cookie 필드 추가
  - [x] 서버에 세션을 관리하는 클래스가 있고, 쿠키로부터 전달 받은 JSESSIONID 값 저장 
  - [x] 쿠키에서 전달 받은 JSESSIONID의 값으로 로그인 여부를 체크할 수 있음
  - [x] 로그인에 성공하면 HttpSession 객체의 값으로 User 객체 저장
  - [x] 로그인된 상태에서 /login 페이지에 HTTP GET method로 접근하면 이미 로그인한 상태니 index.html 페이지로 리다이렉트
