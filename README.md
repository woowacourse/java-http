# 톰캣 구현하기
- [x] 인덱스 페이지(http://localhost:8080/index.html)에 접근할 수 있다
  - [x] 요청 url에 해당하는 파일을 resoucre 디렉토리에서 읽어서 응답으로 보내기
  - [x] Http11ProcessorTest 테스트 클래스의 모든 테스트를 통과
- [x] CSS 지원하기: MIME 형식이 text/css인 경우
  - [x] CSS인 경우 응답 헤더의 Content-Type을 text/css로 전송한다.
        Content-Type은 확장자를 통해 구분할 수도 있으며, 요청 헤더의 Accept를 활용할 수도 있다.
- [x] Query String 파싱
  - [x] http://localhost:8080/login?account=gugu&password=password으로 접속하면 로그인 페이지(login.html)를 보여주도록 만들자.
  - [x] Query String을 파싱해서 아이디, 비밀번호가 일치하면 콘솔창에 로그로 회원을 조회한 결과가 나오도록 만들자.

- [x] 로그인에 성공하면
  - [x] 응답 헤더에 http status code를 302로 반환
  - [x] index.html 로 리다이렉트
- [x] 로그인에 실패하면 401.html로 리다이렉트

- [x] http://localhost:8080/register 로 접속하면 회원가입 페이지를 보옂ㄴ다.
- [x] 페이지를 보여줄 때는 GET을 사용한다.
- 
- [ ] 회원가입을 버튼을 누르면 HTTP method를 GET이 아닌 POST를 사용한다.
- [ ] 회원가입을 완료하면 index.html로 리다이렉트한다.
- [x] 로그인 페이지도 버튼을 눌렀을 때 GET 방식에서 POST 방식으로 전송하도록 변경

- [ ] Cookie 클래스를 추가
- [ ] HTTP Request Header의 Cookie에 JSESSIONID가 없으면 HTTP Response Header에 Set-Cookie를 반환해주는 기능을 구현

- [ ] JSESSIONID의 값으로 로그인 여부를 확인할 수 있다.
- [ ] 로그인에 성공하면 Session 객체의 값으로 User 객체를 저장한다.
- [ ] 로그인된 상태에서 /login 페이지에 HTTP GET method로 접근하면 이미 로그인한 상태니 index.html 페이지로 리다이렉트 처리한다.


