# 톰캣 구현하기

## 1단계 - HTTP 서버 구현하기
- [x] GET /index.html 응답하기
- [x] CSS 지원하기
- [x] Query String 파싱

## 2단계 - 로그인 구현하기
- [x] HTTP Status Code 302
  - [x] 로그인 성공 시, 응답 헤더에 http status code를 302로 반환하고 `/index.html`로 리다이렉트
  - [x] 로그인 실패 시,  `401.html`로 리다이렉트
- [x] POST 방식으로 회원가입
  - [x] `http://localhost:8080/register` 으로 접속하면 회원가입 페이지(`register.html`)를 보여준다.
  - [x] 회원가입 페이지를 보여줄 때는 GET을 사용한다.
  - [x] 회원가입을 버튼을 누르면 HTTP method를 GET이 아닌 POST를 사용한다.
  - [x] 회원가입을 완료하면 `index.html`로 리다이렉트한다.
  - [x] 로그인 페이지도 버튼을 눌렀을 때 GET 방식에서 POST 방식으로 전송하도록 변경하자.
- [ ] Cookie에 JSESSIONID 값 저장하기
  - [ ] 서버에서 HTTP 응답을 전달할 때 응답 헤더에 `Set-Cookie`를 추가하고 `JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46` 형태로 값을 전달하면 클라이언트 요청 헤더의 `Cookie` 필드에 값이 추가된다.
  - [ ] Cookie 클래스를 추가하고 HTTP Request Header의 Cookie에 `JSESSIONID`가 없으면 HTTP Response Header에 `Set-Cookie`를 반환해주는 기능을 구현한다.
