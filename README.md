# 만들면서 배우는 스프링

## 톰캣 구현하기

### 학습목표

- 웹 서버 구현을 통해 HTTP 이해도를 높인다.
- HTTP의 이해도를 높혀 성능 개선할 부분을 찾고 적용할 역량을 쌓는다.
- 서블릿에 대한 이해도를 높인다.
- 스레드, 스레드풀을 적용해보고 동시성 처리를 경험한다.

### 시작 가이드

1. 미션을 시작하기 전에 파일, 입출력 스트림 학습 테스트를 먼저 진행합니다.
    - [File, I/O Stream](study/src/test/java/study)
    - 나머지 학습 테스트는 다음 강의 시간에 풀어봅시다.
2. 학습 테스트를 완료하면 LMS의 1단계 미션부터 진행합니다.

## 학습 테스트

1. [File, I/O Stream](study/src/test/java/study)
2. [HTTP Cache](study/src/test/java/cache)
3. [Thread](study/src/test/java/thread)

### STEP1. HTTP 서버 구현하기

- [x] GET /index.html 응답하기
    - 인덱스 페이지(http://localhost:8080/index.html)에 접근할 수 있도록 만들어야 한다.
    - `Http11ProcessorTest` 테스트 클래스의 모든 테스트를 통과해야 한다.
    - 브라우저에서 요청한 HTTP Request Header는 다음과 같다.
      ```http
      GET /index.html HTTP/1.1
      Host: localhost:8080
      Connection: keep-alive
      Accept: */*
      ```

- [x] CSS 지원하기
    - 사용자가 페이지를 열었을 때 CSS 파일도 호출하도록 기능을 추가해야 한다.
    - 브라우저에서 요청한 HTTP Request Header는 다음과 같다.
    ```http
    GET /css/styles.css HTTP/1.1
    Host: localhost:8080
    Accept: text/css,*/*;q=0.1
    Connection: keep-alive
    ```

- [x] Query String 파싱
    - http://localhost:8080/login?account=gugu&password=password 으로 접속하면 로그인 페이지(login.html)를 보여주도록 만들어야 한다.
    - 로그인 페이지에 접속했을 때 Query String을 파싱해서 아이디, 비밀번호가 일치하면 콘솔창에 로그로 회원을 조회한 결과가 나오도록 만들어야 한다.

### STEP2. 로그인 구현하기

- [ ] HTTP Status Code 302
    - 로그인 여부에 따라 다른 페이지로 이동하도록 만들어야 한다.
    - /login 페이지에서 아이디는 gugu, 비밀번호는 password를 입력했을 때 로그인이 성공하도록 만들어야 한다.
        - 로그인에 성공하면 응답 헤더에 http status code를 302로 반환하고 /index.html로 리다이렉트 한다.
        - 로그인에 실패하면 401.html로 리다이렉트한다.

- [ ] POST 방식으로 회원가입
    - http://localhost:8080/register 으로 접속하면 회원가입 페이지(register.html)를 보여준다.
    - 회원가입 페이지를 보여줄 때는 GET을 사용한다.
    - 회원가입을 버튼을 누르면 HTTP method를 GET이 아닌 POST를 사용한다.
    - 회원가입을 완료하면 index.html로 리다이렉트한다.
    - 로그인 페이지도 버튼을 눌렀을 때 GET 방식에서 POST 방식으로 전송하도록 변경해야 한다.

- [ ] Cookie에 JSESSIONID 값 저장하기
    - 로그인에 성공하면 쿠키와 세션을 활용해서 로그인 상태를 유지해야 한다.
    - HTTP 서버는 세션을 사용해서 서버에 로그인 여부를 저장한다.
    - 세션을 구현하기 전에 먼저 쿠키를 구현해본다.
    - 자바 진영에서 세션 아이디를 전달하는 이름으로 JSESSIONID를 사용한다.
    - 서버에서 HTTP 응답을 전달할 때 응답 헤더에 Set-Cookie를 추가하고 JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 형태로 값을 전달하면 클라이언트 요청
      헤더의 Cookie 필드에 값이 추가된다.
    - 서버로부터 쿠키 설정된 클라이언트의 HTTP Request Header 예시
    ```http
      GET /index.html HTTP/1.1
      Host: localhost:8080
      Connection: keep-alive
      Accept: */*
      Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46
    ```
    - Cookie 클래스를 추가하고 HTTP Request Header의 Cookie에 JSESSIONID가 없으면 HTTP Response Header에 Set-Cookie를 반환해주는 기능을 구현한다.
    ```http
      HTTP/1.1 200 OK 
      Set-Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46
      Content-Length: 5571
      Content-Type: text/html;charset=utf-8;
    ```

- [ ] Session 구현하기
    - 쿠키에서 전달 받은 JSESSIONID의 값으로 로그인 여부를 체크할 수 있어야 한다.
    - 로그인에 성공하면 Session 객체의 값으로 User 객체를 저장해야 한다.
    - 로그인된 상태에서 /login 페이지에 HTTP GET method로 접근하면 이미 로그인한 상태니 index.html 페이지로 리다이렉트 처리한다.


