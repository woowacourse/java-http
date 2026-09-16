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

- [x] 사용자가 페이지를 열었을 때 CSS 파일도 호출한다.

브라우저에서 요청한 HTTP Request Header는 다음과 같다.

```http request
GET /css/styles.css HTTP/1.1
Host: localhost:8080
Accept: text/css,*/*;q=0.1
Connection: keep-alive
``` 

#### 3. Query String 파싱

- [x] http://localhost:8080/login?account=gugu&password=password으로 접속하면 로그인 페이지(login.html)를 보여준다.
- [x] 로그인 페이지 접속 시 Query String을 파싱해서 아이디, 비밀번호 일치를 검사한다.
- [x] 일치 한다면 콘솔창에 로그로 회원을 조회한 결과가 나오게 한다.

## 2단계 - 로그인 구현하기

> 1단계에서 HTML 파일을 출력하는 간단한 웹서버를 만들었다.
> 이제 로그인과 회원가입 기능을 추가해보자.
> 로그인에 필요한 쿠키와 세션도 같이 구현해보자.

### 기능 요구 사항

#### 1. HTTP Status Code 302

- [ ] 로그인 여부에 따라 다른 페이지로 이동한다.
- [ ] 로그인에 성공하면 응답 헤더에 http status code를 302로 반환하고 /index.html로 리다이렉트 한다.
- [ ] 로그인에 실패하면 401.html로 리다이렉트한다.

`InMemoryUserRepository`에는 다음과 같은 정보가 들어있다.

```text
account: "gugu",
password: "password"
```


