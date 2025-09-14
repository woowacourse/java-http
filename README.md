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

## STEP 1
- [x] GET /index.html 응답
  - 인덱스 페이지(http://localhost:8080/index.html)에 접근할 수 있도록 만든다
  - 브라우저에서 요청한 HTTP Request Header는 다음과 같다
```http request
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accept: */*

```

- [x] CSS 지원하기
  - 사용자가 페이지를 열었을 때 CSS 파일도 호출하도록 추가한다
```http request
GET /css/styles.css HTTP/1.1
Host: localhost:8080
Accept: text/css,*/*;q=0.1
Connection: keep-alive

```

- [x] Query String 파싱
  - http://localhost:8080/login?account=gugu&password=password으로 접속하면 로그인 페이지(login.html)를 보여준다
  - 로그인 페이지에 접속했을 때 Query String 파싱해 ID, PW 일치하면 콘솔창에 로그로 회원을 조회한 결과가 나오도록 만든다

## STEP 2

- [x] `/login` 성공하면 302 코드를 반환하고 `/index.html`로 리다이렉트, 실패하면 `401.html` 리다이렉트
- [x] GET /register 응답
  - http://localhost:8080/register 접속하면 회원가입 페이지(register.html)를 보여준다
- [x] 회원가입을 누르면 POST 요청을 보내고 회원가입을 완료하면 index.html로 리다이렉트한다
- [x] 로그인 버튼도 POST 요청으로 변경한다
- [x] 로그인에 성공하면 쿠키와 세션을 활용해서 로그인 상태를 유지한다
  - 세션 아이디를 전달하는 이름으로 JSESSIONID를 사용
- [x] JSESSIONID의 값으로 로그인 여부를 체크한다
  - 로그인 성공 시 Session 객체의 값으로 User 객체를 저장한다

## STEP 3
- [x] `HttpRequest` 클래스 구현
  - `RequestLine` 클래스 추가
- [x] `HttpResponse` 클래스 구현
- [x] `Controller` 인터페이스 추가
  - HTTP 요청, 응답을 다른 객체에게 역할을 맡기고 나니까 uri 경로에 따른 if 분기 처리가 남는다
  - if 분기 리팩터링 -> 컨트롤러 인터페이스를 추가하고 로직마다 `AbstractController`를 상속한 구현체로 만든다
  - HttpRequest 객체 요청을 처리할 컨트롤러 객체를 구현한다
```java
public interface Controller {
    void service(HttpRequest request, HttpResponse response) throws Exception;
}

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        // http method 분기문
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}

public class RequestMapping {
    ...
    public Controller getController(HttpRequest request) {
        ...
    }
}

```

## STEP 4
- [x] ExecutorService로 Thread Pool 관리 적용
- [x] 세션 관리에 동시성 컬렉션 사용
