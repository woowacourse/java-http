# 만들면서 배우는 스프링

## 톰캣 구현하기 기능 요구사항

### 1단계 - HTTP 서버 구현하기

1. GET /index.html 응답하기

- [x] 인덱스 페이지 보여주도록 만들기 (GET `/index.html`)
- [x] Http11ProcessorTest 테스트 클래스의 모든 테스트 통과하기

2. CSS 지원하기

- [x] 인덱스 페이지 열었을 때 CSS 파일도 호출하도록 만들기 (GET `/css/style.css`)

3. Query String 파싱

- [ ] 로그인 페이지 보여주도록 만들기 (GET `/login?account=gugu&password=password`)
- [ ] 로그인 페이지에 접속했을 때 Query String 파싱하기
- [ ] 파싱한 아이디, 비밀번호가 일치하면 콘솔창에 로그로 회원을 조회한 결과가 나오게 만들기

```
user : User{id=1, account='gugu', email='hkkang@woowahan.com', password='password'}
```

### 2단계 - 로그인 구현하기

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
