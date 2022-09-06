# 톰캣 구현하기

## 기능 요구사항 목록
### 1단계 - HTTP 서버 구현하기
- [x] `http://localhost:8080/index.html` 페이지에 접근 가능하다.
- [x] 접근한 페이지의 js, css 파일을 불러올 수 있다.
- [x] uri의 QueryString을 파싱하는 기능이 있다.

### 2단계 - 로그인 구현하기
- [x] 로그인 여부에 따라 다른 페이지로 이동할 수 있다.
  - [x] 로그인에 성공하면 응답 헤더에 http status code를 302로 반환하고 `/index.html`로 리다이렉트 한다.
  - [x] 로그인에 실패하면 응답 헤더에 http status code를 302로 반환하고 `/401.html`로 리다이렉트한다.
- [x] `POST`로 들어온 요청의 Request Body를 파싱할 수 있다.
  - [x] `http://localhost:8080/register` 으로 `GET` 요청 시 회원가입 페이지(`register.html`)를 보여준다.
  - [x] 회원 가입 요청 시 `POST`를 사용하여 요청을 보낸다.
  - [x] 회원 가입 완료 시 `index.html`로 리다이렉트한다.
  - [x] 로그인 요청 시 `POST`를 사용하여 요청을 보낸다.
- [x] 로그인에 성공하면 HTTP Reponse의 헤더에 `Set-Cookie`가 존재한다.
  - [x] HTTP Response Header에 `Set-Cookie`를 반환한다.
- [ ] 서버에 세션을 관리하는 클래스가 있고, 쿠키로부터 전달 받은 `JSESSIONID` 값이 저장된다.
  - [ ] 로그인에 성공하면 Session 객체의 값으로 User 객체를 저장한다.
  - [ ] 로그인된 상태에서 `/login` 페이지에 HTTP GET method로 접근하면 `index.html` 페이지로 리다이렉트한다.
