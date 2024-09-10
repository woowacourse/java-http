## 기능 요구 사항

### 1단계 기능 요구 사항

- [x] GET `/index.html` 응답 기능 구현
    - [x] `http://localhost:8080/index.html` 접속 시 인덱스 페이지(index.html) 응답 기능 구현
- 브라우저에서 요청한 HTTP Request Header

```text
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accept: */*
```

- [x] CSS 지원 기능 구현

```text
GET /css/styles.css HTTP/1.1
Host: localhost:8080
Accept: text/css,*/*;q=0.1
Connection: keep-alive
```

- [x] Query String 파싱 기능 구현
    - [x] `http://localhost:8080/login?account=gugu&password=password` 접속 시 로그인 페이지(login.html) 응답 기능 구현
- [x] 로그인 페이지에 접속했을 때 Query String을 파싱해서 아이디, 비밀번호가 일치하면 콘솔창에 로그로 회원을 조회한 결과를 출력하는 기능 구현

### 2단계 기능 요구 사항

- [x] 로그인 성공 시 응답 헤더에 http status code를 302로 반환하고, `/index.html`로 리다이렉트하는 기능 구현
- [x] 로그인 실패 시 `401.html`로 리다이렉트

- [x] `http://localhost:8080/register` 접속 시 회원가입 페이지(register.html) 응답 기능 구현
    - [x] 회원가입 페이지를 보여줄 때는 HTTP method GET 사용
    - [x] 회원가입을 버튼을 누르면 HTTP method POST 사용
    - [x] 회원가입을 완료하면 index.html로 리다이렉트

- [x] 로그인 버튼을 눌렀을 때 POST 방식으로 전송하도록 변경

- [x] Cookie에 JSESSIONID 값 저장
- [ ] Session 구현
    - [ ] 쿠키에서 전달 받은 JSESSIONID의 값으로 로그인 여부 체크
    - [ ] 로그인된 상태에서 /login 페이지에 접근 시 index.html 페이지로 리다이렉트 처리
