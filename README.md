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

<br>

## 2단계 - 로그인 구현하기

### 기능 요구 사항

- [ ] 로그인 성공 여부에 따라 리다이렉트 한다.
  - [x] 로그인 버튼을 누르면 HTTP method를 POST로 요청한다.
  - [x] 성공하면 http status code를 302로 반환하고, `/index.html`로 리다이렉트 한다.
  - [ ] 실패하면 `401.html`로 리다이렉틓 한다.
- [ ] 회원가입
  - [ ] `http://localhost/register` 로 접속하면 GET 요청으로 `register.html`을 보여준다.
  - [ ] 회원가입 버튼을 누르면 HTTP method를 POST로 요청한다.
  - 회원가입을 완료하면 `index.html`로 리다이렉트 한다.

### 302 Found redirection response

```http
HTTP/1.1 302 Found
Location: https://www.example.com/new-profile-url
Content-Type: text/html; charset=utf-8
Content-Length: 0
```

이 응답을 받은 브라우저는 자동적으로 `Location` 헤더에 적힌 URL로 GET 요청을 보내 유저를 new page로 redirecting 한다.

다만 302 응답을 받은 user agent가 후속 redirection request를 수정할 수 있는데, 이를 방지하려면 응답 이후 메서드 변경이 금지된 307 Temporary Redirect를 사용해야 된다. 
