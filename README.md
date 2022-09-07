# 톰캣 구현하기

## 1단계 요구사항
- [x] GET /index.html 응답하기
- [x] CSS 지원하기
- [x] Query String 파싱

## 2단계 요구사항
- [x] HTTP Status Code 302
  - 로그인에 성공하면 상태코드 302를 반환한다.
- [x] POST 방식으로 회원가입 & 로그인
  - Http Method가 Post이고 Content-Type RequestHeader가 `application/x-www-form-urlencoded` 이면 회원가입/로그인을 한다.
  - Http Methode가 Get이면 회원가입/로그인 페이지로 이동한다.
- [x] Cookie에 JSESSIONID 값 저장하기
  - 회원가입 또는 로그인을 하면 JsessionId를 키로 가진 Cookie를 반환한다.
- [x] Session 구현하기
  - 회원가입 또는 로그인을 하면 세션을 만들고 JsessionId를 Cookie로 전달한다.
  - 로그인 페이지로 접속할 때 JsessionId가 일치하면 index.html을 반환한다.
