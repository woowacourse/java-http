# HTTP 서버 구현하기

## 기능 요구사항
* [x] 서버를 실행시켜서 브라우저로 서버에 접속하면 index.html 페이지를 보여준다.
* [x] Query String을 추가해서 로그인 페이지에 접속했을 때 아이디, 비밀번호가 일치하면 회원을 조회한다.
* [x] http://localhost:8080/login으로 접속하면 로그인 페이지(login.html)를 보여준다.
* [x] 로그인 성공하면 응답 헤더에 http status code를 302로 반환한다.
* [x] http://localhost:8080/register으로 접속하면 회원가입 페이지(register.html)를 보여준다.
* [x] 회원가입을 버튼을 누르면 HTTP method를 GET이 아닌 POST를 사용한다.
* [x] 회원가입을 완료하면 index.html로 리다이렉트한다.
* [x] 클라이언트에서 요청하면 CSS 파일도 제공하도록 수정한다.
