## 기능 요구 사항

- [x] GET `/index.html` 응답 기능 구현
    - [x] `http://localhost:8080/index.html` 접속 시 인덱스 페이지(index.html) 응답 기능 구현
- 브라우저에서 요청한 HTTP Request Header

```text
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accept: */*
```

- [ ] CSS 지원 기능 구현

```text
GET /css/styles.css HTTP/1.1
Host: localhost:8080
Accept: text/css,*/*;q=0.1
Connection: keep-alive
```

- [ ] Query String 파싱 기능 구현
    - [ ] `http://localhost:8080/login?account=gugu&password=password` 접속 시 로그인 페이지(login.html) 응답 기능 구현
- [ ] 로그인 페이지에 접속했을 때 Query String을 파싱해서 아이디, 비밀번호가 일치하면 콘솔창에 로그로 회원을 조회한 결과를 출력하는 기능 구현
