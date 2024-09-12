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

## 기능 요구 사항

### 1단계

- [x] GET /index.html 응답하기
  - 인덱스 페이지(http://localhost:8080/index.html)에 접근
  - Http11ProcessorTest 테스트 클래스의 모든 테스트를 통과
- [x] CSS 지원하기
  - 사용자가 페이지를 열었을 때 CSS 파일을 호출하는 기능 추가
  - Accept를 분석해서 반환 content-type 설정
- Query String 파싱
  - [x] 로그인 페이지 접속 (login.html)
  - [x] Query String을 파싱 후 아이디/비밀번호 일치 확인
  - [x] 일치할 경우, 콘솔창에 로그로 회원을 조회한 결과 출력

### 2딘계

- [x] HTTP Status Code 302 : 로그인 페이지에서 로그인 시도에 따른 응답 처리
  - 로그인 성공 시,
    - [x] http status code 302
    - [x] /index.html 로 리다이렉트
  - 로그인 실패 시,
    - [x] http status code 401
    - [x] /401.html 로 리다이렉트

- POST 방식으로 회원가입
  - [x] 회원가입 페이지 접근(http://localhost:8080/register) (페이지 로드 -> GET)
  - [x] 회원가입 완료 -> 저장 및 /index.html로 리다이렉트 (회원가입 버튼 -> POST)

- Cookie에 JSESSIONID 값 저장하기 : 쿠키를 사용해 서버에 로그인 여부 저장
  - [x] 서버에서 HTTP 응답을 전달할 때 응답 헤더에 Set-Cookie를 추가
  - [x] Cookie에 JSESSIONID가 없으면 HTTP Response Header에 Set-Cookie 반환 기능 구현

- Session 구현하기 : 쿠키에서 전달 받은 JSESSIONID의 값으로 로그인 여부 체크
  - 로그인 성공 시,
  - [x] Session 객체의 값으로 User 객체 저장
  - [x] /login 페이지에 HTTP GET method로 접근 시, index.html 페이지로 리다이렉트 처리

### 3단계

- [x] HttpRequest 클래스 구현
  - [x] Request Line 분석
    - [x] Method 클래스 (enum)
    - [x] Path 클래스
    - [x] VersionOfProtocol 클래스
  - [x] Headers 분석
  - [x] body 클래스

- [x] HttpResponse 클래스 구현
  - [x] Status-Line 분석
    - [x] VersionOfProtocol 클래스
    - [x] StatusCode 클래스 (enum) [code + message]
  - [x] headers 분석

- Controller 인터페이스 추가
  - [x] 컨트롤러 인터페이스 제작
  - [x] 컨트롤러를 구현한 추상 클래스 구현
  - [x] 도메인에 맞는 컨트롤러 구현
    - [x] 루트 컨트롤러
      - get /
    - [x] 페이지 로드 컨트롤러
      - get /*.html (* : 페이지 이름)
    - [x] 로그인 컨트롤러
      - get /login
      - post /login?account=gugu&password=password
    - [x] 회원가입 컨트롤러
      - get /register
      - post /register
