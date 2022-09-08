# 톰캣 구현하기

- [x] http://localhost:8080/index.html 페이지에 접근 가능하다.
- [x] 접근한 페이지의 js, css 파일을 불러올 수 있다.
- [x] uri의 QueryString을 파싱하는 기능이 있다.

- [x] HTTP Reponse의 상태 응답 코드를 302로 반환한다.
- [x] POST로 들어온 요청의 Request Body를 파싱할 수 있다.
- [x] 로그인에 성공하면 HTTP Reponse의 헤더에 Set-Cookie가 존재한다.
- [x] 서버에 세션을 관리하는 클래스가 있고, 쿠키로부터 전달 받은 JSESSIONID 값이 저장된다.

- [x] HTTP Request, HTTP Response 클래스로 나눠서 구현했다.
- [x] Controller 인터페이스와 RequestMapping 클래스를 활용하여 if절을 제거했다.

- [x] Executors로 만든 ExecutorService 객체를 활용하여 스레드 처리를 하고 있다.
