# HTTP 서버 구현하기

- [ ] 서버를 실행시켜 브라우저로 서버(http://localhost:8080/index.html)에 접속하면 index.html 페이지를 보여준다.
- [ ] http://localhost:8080/login으로 접속하면 로그인 페이지(login.html)를 보여준다.
    - [ ] Query String을 추가해서 로그인 페이지에 접속했을 때 아이디, 비밀번호가 일치하면 회원을 조회한다.
- [ ] 회원을 조회해서 로그인에 성공하면 /index.html로 리다이렉트한다.
    - [ ] /login?account=gugu&password=password로 접근해서 로그인 성공하면 응답 헤더에 http status code를 302로 반환한다.
    - [ ] 로그인에 실패하면 401.html로 리다이렉트한다.
- [ ] http://localhost:8080/register으로 접속하면 회원가입 페이지(register.html)를 보여준다.
    - [ ] 회원가입 페이지를 보여줄 때는 GET을 사용한다.
    - [ ] 회원가입을 버튼을 누르면 HTTP method를 GET이 아닌 POST를 사용한다.
    - [ ] 회원가입을 완료하면 index.html로 리다이렉트한다.
    - [ ] 로그인도 버튼을 눌렀을 때 GET 방식에서 POST 방식으로 전송하도록 변경하자.
- [ ] CSS 지원
- [ ] 404.html 연결
- [ ] favicon.io 연결
- [ ] jacoco 이용한 테스트 커버리지 검사