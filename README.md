# 톰캣 구현하기

## HTTP 서버 구현 요구사항

## step1
- [x] `http://localhost:8080/index.html` 페이지에 접근 가능하다.
- [x] 접근한 페이지의 `js`, `css` 파일을 불러올 수 있다.
- [x] `uri`의 `QueryString`을 파싱하는 기능이 있다.

## step2
- [x] HTTP Status Code 302
  - [x] /login 을 get 요청하면 login.html을 보여준다.
  - [x] /login?account=gugu&password=password 을 POST 요청하면 로그인 처리를 한다.
  - [x] 로그인에 실패하면 http status code를 401로 반환하고 /401.html 로 리다이렉트 한다.
  - [x] 로그인에 성공하면 http status code를 302로 반환하고 /index.html 로 리다이렉트 한다.

- [x] POST 방식으로 회원가입
- [x] Cookie에 JSESSIONID 값 저장하기
- [ ] Session 구현하기
