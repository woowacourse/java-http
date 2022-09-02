# 🐱 톰캣 구현하기 🐱

## 🚀 미션 설명

간단한 HTTP 서버를 만들어보자.

저장소에서 소스코드를 받아와서 메인 클래스를 실행하면 HTTP 서버가 실행된다. 

웹브라우저로 로컬 서버(http://localhost:8080)에 접속하면 Hello world!가 보인다.

정상 동작을 확인했으면 새로운 기능을 추가해보자.

## ⚙️ 기능 요구 사항

### 1. GET /index.html 응답하기

인덱스 페이지(http://localhost:8080/index.html)에 접근할 수 있도록 만들자.

Http11ProcessorTest 테스트 클래스의 모든 테스트를 통과하면 된다.

브라우저에서 요청한 HTTP Request Header는 다음과 같다.

```text
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accept: */*
```

### 2. CSS 지원하기

인덱스 페이지에 접속하니까 화면이 이상하게 보인다.

개발자 도구를 열어서 에러 메시지를 체크해보니 브라우저가 CSS를 못 찾고 있다.

사용자가 페이지를 열었을 때 CSS 파일도 호출하도록 기능을 추가하자.

```text
GET /css/styles.css HTTP/1.1
Host: localhost:8080
Accept: text/css,*/*;q=0.1
Connection: keep-alive
```

### 3. Query String 파싱

http://localhost:8080/login?account=gugu&password=password으로 접속하면 로그인 페이지(login.html)를 보여주도록 만들자.

그리고 로그인 페이지에 접속했을 때 Query String을 파싱해서 아이디, 비밀번호가 일치하면 회원을 조회한 결과가 나오도록 만들자.

## 🖊 체크리스트

- [x] http://localhost:8080/index.html 페이지에 접근 가능하다.
- [x] 접근한 페이지의 js, css 파일을 불러올 수 있다.
- [x] uri의 QueryString을 파싱하는 기능이 있다.

## 🖥 기능 목록

- [x] GET /index.html 응답하기
- [x] 예외 처리후 404.html 응답하기
- [x] CSS 지원하기
- [x] Query String 파싱
