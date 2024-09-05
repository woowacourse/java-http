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

---

## 🚀 1단계 - HTTP 서버 구현하기

### 1. GET /index.html 응답하기
- [x] 인덱스 페이지(http://localhost:8080/index.html)에 접근할 수 있도록 만들자.
- [x] `Http11ProcessorTest` 테스트 클래스의 모든 테스트를 통과해야 한다.

### 2. CSS 지원하기
- [x] 사용자가 페이지를 열었을 때 CSS 파일도 호출하도록 기능을 추가하자.


### 3. Query String 파싱
- [x] http://localhost:8080/login?account=gugu&password=password으로 접속하면 로그인 페이지(login.html)를 보여주도록 만들자.
- [x] 로그인 페이지에 접속했을 때 Query String을 파싱해서 아이디, 비밀번호가 일치하면 콘솔창에 로그로 회원을 조회한 결과가 나오도록 만들자.


## 🚀 2단계 - 로그인 구현하기

### 1. HTTP Status Code 302
- [ ] 로그인 여부에 따라 다른 페이지로 이동
  - [ ] 성공하면 응답 헤더에 http status code를 302로 반환하고 /index.html로 리다이렉트 한다.
  - [ ] 실패하면 401.html로 리다이렉트한다.

### 2. POST 방식으로 회원가입
- [ ] http://localhost:8080/register으로 접속하면 회원가입 페이지(register.html)를 보여준다.
- [ ] 회원가입 페이지를 보여줄 때는 GET을 사용한다.
- [ ] 회원가입을 버튼을 누르면 HTTP method를 GET이 아닌 POST를 사용한다.
- [ ] 회원가입을 완료하면 index.html로 리다이렉트한다.
- [ ] 로그인 페이지도 버튼을 눌렀을 때 GET 방식에서 POST 방식으로 전송하도록 변경하자.

### 3. Cookie에 JSESSIONID 값 저장하기
- [ ] 서버에서 HTTP 응답을 전달할 때 응답 헤더에 Set-Cookie를 추가하고 `JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46` 형태로 값을 전달한다.
- [ ] Cookie 클래스를 추가하고 HTTP Request Header의 Cookie에 `JSESSIONID`가 없으면 HTTP Response Header에 Set-Cookie를 반환해주는 기능을 구현한다.

### 4. Session 구현하기
- [ ] 쿠키에서 전달 받은 `JSESSIONID`의 값으로 로그인 여부를 체크할 수 있어야 한다.
- [ ] 로그인에 성공하면 Session 객체의 값으로 User 객체를 저장해보자.
- [ ] 로그인된 상태에서 /login 페이지에 HTTP GET method로 접근하면 이미 로그인한 상태니 index.html 페이지로 리다이렉트 처리한다.
