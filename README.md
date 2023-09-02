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
