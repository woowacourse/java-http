# 톰캣 구현하기
## 1단계 - HTTP 서버 구현하기
- [x] `GET /index.html` 응답하기
  - [x] http://localhost:8080/index.html 페이지에 접근 가능하다.
- [x] CSS 지원하기
  - [x] 접근한 페이지의 js, css 파일을 불러올 수 있다.
- [x] Query String 파싱
  - [x] uri의 QueryString을 파싱하는 기능이 있다.

## 2단계 - 로그인 구현하기
- [ ] HTTP Status Code 302
  - [x] `/login` 경로를 요청시 로그인 페이지를 반환해야 한다.
  - [x] 페이지에 id, password를 입력 후 제출을 하였을 때, 로그인에 성공하면 응답 헤더에 Http status code 302를 반환하고 `/index.html`로 리다이렉트 한다.
  - [ ] 로그인에 실패하면 `401.html`로 리다이렉트한다.
- [ ] Post 방식으로 회원가입 
  - [ ] http://localhost:8080/register 으로 접속하면 회원가입 페이지(register.html)를 보여준다. (GET 메서드 사용)
  - [ ] 회원가입 버튼을 누르면 POST 메서드로 회원가입을 한다.
  - [ ] 회원가입이 완료되면 `index.html`로 리다이렉트 한다.
  - [ ] 로그인 페이지에서도 로그인 버튼을 GET에서 POST로 변경한다.
- [ ] Cookie에 JSESSIONID 값 저장하기
  - [ ] 로그인에 성공하면 쿠키와 세션을 활용해서 로그인 상태를 유지해야 한다.
    - HTTP 서버의 세션을 사용하여 로그인 여부를 저장한다. 그 전에 먼저 쿠키를 구현한다.
  - [ ] 세션 아이디를 전달하는 이름으로는 `JSESSIONID`를 사용한다. 서버에서 HTTP 응답을 전달할 때 응답 헤더에 `Set-Cookie`를 추가하고 `JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46` 형태로 값을 전달하면 클라이언트 요청 헤더의 Cookie 필드에 값이 추가된다.
     ```
    GET /index.html HTTP/1.1
    Host: localhost:8080
    Connection: keep-alive
    Accept: */*
    Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46
    ```
  - [ ] Cookie 클래스를 추가하고 HTTP Request Header의 Cookie에 `JSESSIONID`가 없으면 HTTP Response Header에 `Set-Cookie`를 반환해주는 기능을 구현한다.
     ```
    HTTP/1.1 200 OK 
    Set-Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46
    Content-Length: 5571
    Content-Type: text/html;charset=utf-8;
    ```
- [ ] Session 구현하기
  - [ ] 쿠키에서 전달 받은 JSESSIONID 값으로 로그인 여부를 체크한다.
  - [ ] 로그인에 성공하면 Session 객체의 값으로 User객체를 저장한다.
  - [ ] 로그인된 상태에서 `/login` 페이지에 HTTP GET method로 접근하면 이미 로그인한 상태니 `index.html` 페이지로 리다이렉트 처리한다.
