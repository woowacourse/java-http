## 1단계 - HTTP 서버 구현하기

- [x] GET /index.html 응답하기
- [x] CSS 지원하기
- [x] Query String 파싱

## 2단계 - 로그인 구현하기

- [x] HTTP Status Code 302
  - [x] 로그인 성공 시 http status code 302 반환
  - [x] 로그인 성공 시 `/index.html`로 리다이렉트, 실패 시 `/401.html`로 리다이렉트
- [x] POST 방식으로 회원가입
  - [x] `/register` 접속하면 회원가입 페이지(register.html) 응답
  - [x] 회원가입 페이지를 보여줄 때는 GET 사용, 회원가입 버튼을 누르면 POST 사용
  - [x] 회원가입 완료 시 `/index.html`로 리다이렉트
  - [x] 로그인 페이지에서도 로그인 버튼을 누르면 POST 사용
- [ ] Cookie에 JSESSIONID 값 저장하기
  - [ ] Cookie 클래스 추가
  - [x] HTTP Request Header의 Cookie에 JSESSIONID가 없으면 Set-Cookie 반환
- [ ] Session 구현하기
  - [ ] 로그인 성공 시 Session 객체의 값으로 User 객체 저장 
  - [ ] 로그인된 상태에서 `/login` 접속하면 `index.html`로 리다이렉트
