# 1단계 - HTTP 서버 구현하기

- [x] GET /index.html 응답하기
  - [x] Http11Processor 의 InputStream 사용하여 요청 처리
- [x] CSS 파일 요청 처리
- [x] 로그인 요청에 대해 Query String 파싱하여 login.html 응답

# 2단계 - 로그인 구현하기

- [x] 로그인 기능 구현
  - [x] 성공 시 302 응답과 함께 `/index.html` 로 리다이랙트
  - [x] 실패 시 `401.html` 로 리다이랙트
- [x] 회원가입 기능 구현
  - [x] `localhost:8080/register` 로 GET 요청 시 `register.html` 응답
  - [x] 회원가입 버튼은 POST 요청 사용
  - [x] POST 요청 시 Request Body 받음
  - [x] 로그인 요청 또한 POST 로 변경
- [x] 쿠키에 JSESSIONID 저장
  - [x] 응답 헤더에 `Set-Cookie` 키에 `JSESSIONID=...` 값을 저장
- [x] Session 구현하기
  - [x] `Session` 객체의 키 값으로 `User` 객체 저장
  - [x] 로그인 된 상태에서 `/login` 으로 GET 요청 시 `/index.html` 로 리다이랙트

# 3단계 - 리팩터링

- [x] Request, Response 객체 분리
- [ ] Controller 인터페이스 추가
