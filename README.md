# 만들면서 배우는 스프링

## 톰캣 구현하기 기능 요구사항

### 1단계 - HTTP 서버 구현하기

1. GET /index.html 응답

- [x] 인덱스 페이지(`index.html`) 보여주도록 만들기 (`GET /index.html`)
- [x] Http11ProcessorTest 테스트 클래스의 모든 테스트 통과하기

2. CSS 지원

- [x] 인덱스 페이지 열었을 때 CSS 파일도 호출하도록 만들기 (`GET /css/style.css`)

3. Query String 파싱

- [x] 로그인 페이지(`login.html`) 보여주도록 만들기 (`GET /login?account=gugu&password=password`)
- [x] 로그인 페이지에 접속했을 때 Query String 파싱하기
- [x] 파싱한 아이디, 비밀번호가 일치하면 콘솔창에 로그로 회원을 조회한 결과가 나오게 만들기

### 2단계 - 로그인 구현하기

1. HTTP Status Code 302 반환

- [ ] 로그인 여부에 따라 다른 페이지로 이동시키기
    - [ ] 로그인 성공 시 http status code를 302로 반환하고 `index.html`로 리다이렉트
    - [ ] 로그인 실패 시 `401.html`로 리다이렉트

2. POST 방식으로 회원가입

- [ ] 회원가입 페이지(`register.html`) 보여주도록 만들기 (`GET /register`)
- [ ] 회원가입 버튼을 누르면 회원가입 시키기 (`POST /register`)
    - [ ] 회원가입 성공 시 `index.html`로 리다이렉트
- [ ] 로그인 페이지 보여줄 때와 로그인 버튼 누를 때 API 분리하기 (`GET /login`, `POST /login`)
    - [ ] `login.html` form 태그 수정

3. Cookie에 JSESSIONID 값 저장

- [ ] 쿠키 활용해서 로그인 상태 유지하기 (쿠키에 JSESSIONID 값 저장)
    - [ ] Cookie 클래스 추가
    - [ ] 요청 헤더의 Cookie에 JSESSIONID가 없으면, 응답 헤더에 Set-Cookie 추가해 JSESSIONID 반환

4. Session 구현

- [ ] 세션 활용해서 로그인 상태 유지하기 (세션을 사용해서 서버에 로그인 여부를 저장)
    - [ ] 쿠키에서 전달 받은 JSESSIONID 값으로 로그인 여부 체크
    - [ ] SessionManager, Session 클래스 추가
    - [ ] 로그인 성공 시 Session 객체의 값으로 User 객체를 저장
    - [ ] 로그인된 상태에서 로그인 페이지 접근 시 index.html 페이지로 리다이렉트 (`GET /login`)

### 3단계 - 리팩터링

### 4단계 - 동시성 확장하기

## 톰캣 구현하기 가이드

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
