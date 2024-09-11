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


### 1단계 - HTTP 서버 구현하기
- [x] 인덱스 페이지(http://localhost:8080/index.html)에 접근할 수 있다.
- [x] CSS 지원
- [x] String Query 파싱

### 2단계 - 로그인 구현하기
- [x] 로그인 여부에 따라 다른 페이지로 이동
  - [x] `/login` 페이지에서 아이디는 `gugu`, 비밀번호는 `password`를 입력
  - [x] 로그인에 성공하면 응답 헤더에 http status code를 `302`로 반환하고 `/index.html`로 리다이렉트
  - [x] 로그인에 실패하면 `401.html`로 리다이렉트한다.
- [x] http://localhost:8080/register 으로 접속하면 회원가입 페이지`register.html` 반환
  - [x] 회원가입 페이지를 보여줄 때는 `GET`을 사용
  - [X] 회원가입을 버튼을 누르면 HTTP method를 `GET`이 아닌 `POST`를 사용
  - [X] 회원가입을 완료하면 `index.html`로 리다이렉트
  - [x] 로그인 페이지도 버튼을 눌렀을 때 `GET` 방식에서 `POST` 방식으로 전송하도록 변경
- [x] 로그인에 성공하면 쿠키와 세션을 활용해서 로그인 상태를 유지
  - [x] 세션을 구현하기 전에 먼저 쿠키를 구현
    - [x] 자바 진영에서 세션 아이디를 전달하는 이름으로 JSESSIONID를 사용
    - [x] Cookie 클래스를 추가
    - [x] HTTP Request Header의 Cookie에 JSESSIONID가 없으면 HTTP Response Header에 Set-Cookie를 반환해주는 기능을 구현
- [x] Session 구현
  - [x] 쿠키에서 전달 받은 JSESSIONID의 값으로 로그인 여부를 체크
  - [x] 로그인에 성공하면 Session 객체의 값으로 User 객체를 저장
  - [x] 로그인된 상태에서 /login 페이지에 HTTP GET method로 접근하면 이미 로그인한 상태니 index.html 페이지로 리다이렉트

### 3단계 리팩토링
- [x] HttpRequest 구현하기
- [x] HttpResponse 구현하기
- [ ] Controller 인터페이스 추가하기
  - [ ] 컨트롤러 인터페이스를 추가하고 각 분기에 있는 로직마다 AbstractController를 상속한 구현체로 만들기
