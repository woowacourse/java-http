# HTTP 서버 구현하기

## 기능 요구사항
- [x] GET /index.html 응답하기
- [x] Query String 파싱
  - [x] /login으로 접속하면 로그인 페이지(login.html) 보여준다.
  - [x] 추출한 request uri에서 데이터를 추출해 회원정보 확인
- [x] HTTP Status Code 302
  - [x] 회원을 조회해서 로그인에 성공하면 /index.html로 리다이렉트
  - [x] 로그인 성공 시 응답 헤더에 http status code를 302로 반환
  - [x] 로그인 실패 시 401.html로 리다이렉트
- [x] POST 방식으로 회원가입
  - [x] /register로 접속하면 회원가입 페이지(register.html) 보여준다.
  - [x] 회원가입을 완료하면 index.html로 리다이렉트
- [x] CSS 지원하기
  - [x] 클라이언트에서 요청하면 CSS파일도 제공하도록 수정
  - [x] 클라이언트에서 요청하는 js, img 파일 모두 제공하도록 수정
  - [x] 루트경로(/)로 접속하면 index.html 보여준다.
