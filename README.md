# 톰캣 구현하기

1단계 HTTP 서버 구현하기
-[x] HTTP 서버 구현하기
  - [x] GET /index.html 응답하기
  - [x] CSS 지원하기
  - [x] Query String 파싱

2단계 로그인 구현하기
- [x] HTTP Status Code 302
  - [x] 로그인 성공시 302반환 및 /index.html로 리다이렉트
  - [x] 로그인 실패 시 401.html로 리다이렉트
- [x] POST 방식으로 회원가입
  - [x] 회원가입 완료 시 index.html로 리다이렉트
  - [x] 로그인 페이지도 POST 방식
- [x] Cookie에 JSESSIONID 값 저장
- [x] Session 구현

3단계 리팩터링
- [x] HttpRequest 구현
- [x] HttpResponse 구현
- [ ] Controller 구현
