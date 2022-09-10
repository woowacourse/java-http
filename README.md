# 톰캣 구현하기

## 1단계 - HTTP 서버 구현하기

1. GET /index.html 응답하기
- [x] http://localhost:8080/index.html 에 접근할 수 있도록 만든다.

2. CSS 지원하기
- [x] CSS 파일도 호출할 수 있도록 기능을 추가한다.
- [x] javascript 파일도 호출할 수 있도록 기능을 추가한다.

3. Query String 파싱
- [x] http://localhost:8080/login?account=gugu&password=password으로 접속하면 로그인 페이지(login.html)를 보여주도록 만든다.
- [x] 로그인 페이지에 접속했을 때 Query String을 파싱해서 아이디, 비밀번호가 일치하면 회원을 조회한 결과가 나오도록 만든다.

## 2단계 - 로그인 구현하기

1. HTTP Status Code 302
- [x] `/login` 을 요청하면 로그인 페이지가 응답되도록 한다.
- [x] 로그인에 성공하면 `/index.html`로 리다이렉트한다.
- [x] 로그인에 실패하면 `/401.html`로 리다이렉트한다.

2. POST 방식으로 회원가입
- [x] http://localhost:8080/register으로 접속하면 회원가입 페이지(register.html)를 보여준다.
- [x] 회원가입 버튼을 누르면 `POST`로 요청을 처리한다.
- [x] 회원가입을 완료하면 `/index.html`로 리다이렉트한다.

3. Cookie에 JSESSIONID 값 저장하기
- [x] 로그인에 성공하면 HTTP Reponse의 헤더에 Set-Cookie가 존재한다.
- [x] 로그인에 성공하면 쿠키와 세션을 활용해 로그인 상태를 유지한다.

4. Session 구현하기
- [x] 쿠키에서 전달 받은 JSESSIONID의 값으로 로그인 여부를 체크할 수 있게 한다.
- [x] 로그인된 상태에서 /login 페이지에 HTTP GET method로 접근하면 `/index.html` 페이지로 리다이렉트 처리한다.

## 3단계 - 리팩터링

1. HttpRequest 클래스 구현하기
- [x] HTTP 요청을 처리하는 클래스를 추가한다

2. HttpResponse 클래스 구현하기
- [x] HTTP 응답을 처리하는 클래스를 추가한다.

3. Controller 인터페이스 추가하기
- [x] HttpRequest 를 처리하는 Controller 인터페이스를 추가한다.

## 4단계 - 동시성 확장하기

1. Executors로 Thread Pool 적용
- [x] 요청마다 스레드를 생성하는 구조에서, 미리 스레드를 생성하고 할당하는 방식으로 바꾼다.

2. 동시성 컬렉션 사용하기
- [x] 기존 HashMap 컬렉션은 동시성 이슈가 발생하므로, ConcurrentHashMap을 사용한다.

