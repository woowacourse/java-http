# HTTP 서버 구현하기

## 1단계 요구사항
- [X] 서버를 실행시켜 브라우저로 서버(http://localhost:8080/index.html)에 접속하면 index.html 페이지를 보여준다.
- [X] http://localhost:8080/login으로 접속하면 로그인 페이지(login.html)를 보여준다.
    - [X] Query String을 추가해서 로그인 페이지에 접속했을 때 아이디, 비밀번호가 일치하면 회원을 조회한다.
- [X] 회원을 조회해서 로그인에 성공하면 /index.html로 리다이렉트한다.
    - [X] /login?account=gugu&password=password로 접근해서 로그인 성공하면 응답 헤더에 http status code를 302로 반환한다.
    - [X] 로그인에 실패하면 401.html로 리다이렉트한다.
- [X] http://localhost:8080/register으로 접속하면 회원가입 페이지(register.html)를 보여준다.
    - [X] 회원가입 페이지를 보여줄 때는 GET을 사용한다.
    - [X] 회원가입을 버튼을 누르면 HTTP method를 GET이 아닌 POST를 사용한다.
    - [X] 회원가입을 완료하면 index.html로 리다이렉트한다.
    - [X] 로그인도 버튼을 눌렀을 때 GET 방식에서 POST 방식으로 전송하도록 변경하자.
- [X] CSS 지원
- [X] 404.html 연결
- [X] favicon.io 연결
- [X] jacoco 이용한 테스트 커버리지 검사

## 2,3단계 요구사항
- [ ] 로그인에 성공하면 쿠키와 세션을 활용해서 로그인 상태를 유지해야 한다.
- [ ] Cookie 클래스를 추가한다.
  - [ ] HTTP Request Header의 Cookie에 JSESSIONID가 없으면 HTTP Response Header에 Set-Cookie를 반환해주는 기능을 구현한다.
  
- [ ] Session 을 구현한다. 
  - [ ] 쿠키에서 전달 받은 JSESSIONID의 값으로 로그인 여부를 체크할 수 있어야 한다.
  - [ ] 로그인에 성공하면 HttpSession 객체의 값으로 User 객체를 저장해보자.
  - [ ] 로그인된 상태에서 /login 페이지에 HTTP GET method로 접근하면 이미 로그인한 상태니 index.html 페이지로 리다이렉트 처리한다.
  - [ ] 클라이언트별 세션 데이터를 관리하는 세션 클래스를 추가한다.