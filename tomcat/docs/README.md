## 1단계 - HTTP 서버 구현하기

- [X] GET /index.html 요청이 들어오면, index.html을 반환한다.
### Request
```http request
  GET /index.html HTTP/1.1
    Host: localhost:8080
    Connection: keep-alive
    Accept: */*
  ```

- [X] GET /index.html 요청이 들어오면, 관련된 js, css 파일도 반환한다.
### Request
```http request
GET /css/styles.css HTTP/1.1
Host: localhost:8080
Accept: text/css,*/*;q=0.1
Connection: keep-alive
```

- [X] Query String을 파싱한다.
  - [X] Login Page를 반환한다.
  - [X] 로그인이 가능(아이디, 비밀번호 일치)하면, 콘솔 로그에 회원 정보를 출력한다.

## 2단계 - 로그인 구현하기

- [ ] 로그인 여부에 따라 다른 페이지로 리다이렉트 한다.
  - [ ] 로그인에 성공하면, 302 status code와 함께 /index.html로 리다이렉트 한다.
  - [ ] 로그인에 실패하면, 302 status code와 함께 /401.html로 리다이렉트 한다.
  
- [ ] 회원가입
  - [ ] GET /register 요청이 들어오면, register.html을 반환한다.
  - [ ] 회원가입 버튼을 누르면 POST 요청을 보낸다.
  - [ ] 회원가입이 완료되면, index.html로 리다이렉트 한다.

- [ ] Cookie에 JSESSIONID 값 저장하기
  - [ ] 요청 헤더에 JSESSIONID가 없을 경우, 응답 헤더에 Set-Cookie를 추가한다.

- [ ] Session 구현하기
  - [ ] 쿠키에서 전달받은 JSESSIONID 값으로 로그인 여부를 체크한다.
  - [ ] 로그인에 성공하면, SESSION 객체의 값으로 USER를 저장한다.
  - [ ] 로그인 된 상태에서 /login 페이지에 GET 요청을 보낼 경우, /index.html로 리다이렉트 한다.