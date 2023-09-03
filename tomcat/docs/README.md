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