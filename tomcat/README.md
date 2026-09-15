# Tomcat 구현하기

## 1단계 - HTTP 서버 구현하기

> 간단한 HTTP 서버를 만들어보자.
> 저장소에서 소스코드를 받아와서 메인 클래스를 실행하면 HTTP 서버가 실행된다.
> 웹브라우저로 로컬 서버(http://localhost:8080) 에 접속하면 Hello world!가 보인다.
> 정상 동작을 확인했으면 새로운 기능을 추가해보자.

### 기능 요구 사항

#### 1. GET /index.html 응답하기

- [x] 인덱스 페이지(http://localhost:8080/index.html)에 접근할 수 있다.
- [x] Http11ProcessorTest 테스트 클래스의 모든 테스트를 통과한다.

브라우저에서 요청한 HTTP Request Header는 다음과 같다.

```http request
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accept: */*
```

#### 2. CSS 지원하기

- [ ] 사용자가 페이지를 열었을 때 CSS 파일도 호출한다.

브라우저에서 요청한 HTTP Request Header는 다음과 같다.

```http request
GET /css/styles.css HTTP/1.1
Host: localhost:8080
Accept: text/css,*/*;q=0.1
Connection: keep-alive
``` 
