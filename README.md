# 톰캣 구현하기

---

## 1단계 구현 내용 정리

- [x] GET /index.html 응답하기
  - [x] `http://localhost:8080/index.html` 요청시 index.html 파일을 응답한다.
  - [x] `http://localhost:8080` 으로 요청시 index.html 을 기본으로 응답해준다.
  - [x] 파일 확장자가 명시되지 않은 경우 `.html` 을 붙인 파일을 응답한다.
- [x] CSS 지원하기
  - [x] Accept 에 명시한 MIME 타입으로 반해줄 수 있다.
- [x] Query String 파싱
  - [x] Query String 이 포함된 요청인 경우 이를 처리해준다. 구체적으로 account 로 InMemory DB에 미리 저장되어 있는 사용자를 조회하여 로그를 남긴다. 

## 새롭게 알게 된 내용 (1단계)

### ClassLoader.getSystemResource

`ClassLoader.getSystemResource()` 메소드를 활용해서 간단하게 Class Path 에서 Resource를 찾는 방법을 배우게 되었다.
클래스를 로딩하기 위해서는 class 파일을 바이트로 읽어서 메모리에 로딩하게 된다.
설정 파일이나 다른 파일들은 바이트로 읽기 위해서 `InputStream` 을 얻어야 한다.
즉, 클래스 패스에 존재하는 모든 클래스 파일들, 설정 파일, 그 외 파일들 등등 모든 파일들은 `ClassLoader` 에서 찾을 수 있다.

참고 : [[JAVA] CLASS PATH에서 RESOURCE 찾기](https://whitecold89.tistory.com/9#recentEntries)

### 로그 테스트

1단계 미션에서 다음과 같은 요구사항을 구현하였다.

```
http://localhost:8080/login?account=gugu&password=password으로 접속하면 로그인 페이지(login.html)를 보여주도록 만들자.
그리고 로그인 페이지에 접속했을 때 Query String을 파싱해서 아이디, 비밀번호가 일치하면 회원을 조회한 결과가 나오도록 만들자.
```

그런데, 여기서 회원을 조회한 이후에 `로그` 를 통해서 회원을 조회한 결과를 확인하게 된다.

따라서 로그가 제대로 찍혔는지를 확인하는 테스트 코드를 작성하는 법에 대해서 고민하게 되었고,
다음의 글을 참고하여 로그 또한 테스트 코드에서 확인해볼 수 있음을 알게 되었다.

참고 : [추가된 LOG를 JUnit에서 확인하는 방법](https://blog.advenoh.pe.kr/java/%EC%B6%94%EA%B0%80%EB%90%9C-LOG%EB%A5%BC-JUnit-%EC%97%90%EC%84%9C-%ED%99%95%EC%9D%B8%ED%95%98%EB%8A%94-%EB%B0%A9%EB%B2%95/)
<br>
참고 : [JUnit을 이용한 Log 메시지 테스트](https://xlffm3.github.io/java/log-junit-test/)

### Content-Type

HTTP 응답 헤더에 실어지는 `Content-Type` 이 무엇을 하는지에 대해서 알 수 있었다.
`Content-Type` 헤더는 리소스의 `media type` 을 나타내기 위한 헤더로 클라이언트에게 반환되는 컨텐츠의 컨텐츠 유형이 실제로 무엇인지를 나타낸다.
(브라우저들은 어떤 경우에는 MIME 스니핑을 해서 이 헤더의 값을 꼭 따르지는 않는다.)
css 파일과 같은 것을 응답으로 반환해줄 때에는 'text/css' 와 같이 지정해주어야 브라우저에서 정확하게 인식할 수 있음으로 특히 주의해야 한다.
또한 클라이언트 입장에서는 HTTP 요청 헤더의 `Accept` 를 통해서 응답으로 받길 원하는 컨텐츠 형식을 지정해줄 수 있는데,
이 때 우선순위에 따라서 여러개를 명시해줄 수 있게 된다.

참고 : [RFC 문서 Accept](https://www.rfc-editor.org/rfc/rfc2616#page-100)

---

## 2단계 구현 내용 정리

- [x] HTTP Status Code 302
  - [x] `http://localhost:8080/login` 요청시 login.html 파일을 응답한다.
  - [x] `http://locahost:8080/login` 페이지에서 입력한 아이디와 비밀번호는 query string으로 전달된다.
  - [x] `http://localhost:8080/login` 페이지에서 로그인 버튼 클릭시 302 상태코드를 응답한다.
  - [x] 로그인 성공시에는 index.html로 리다이렉트한다.
  - [x] 로그인 실패시에는 401.html로 리다이렉트한다.
  

- [x] POST 방식으로 회원가입
  - [x] 기존 로그인 POST 방식으로 변경
    - [x] `login.html` form 태그 변경
    - [x] 기존 로그인 방식을 `POST` 로 변경
    - [x] `HttpResopnse` 에서 responseBody 생성하도록 변경
    - [x] `StartLine` 및 `HttpMethod` 도출
    - [x] 기존 쿼리파라미터를 받던 `LoginHandler` 에서 바디를 읽도록 수정
    - [x] `ContentLength` 헤더를 이용해 POST 요청의 바디를 읽도록 구현
  - [x] 회원가입 처리
    - [x] `http://localhost:8080/register` GET 요청시 register.html 을 응답한다.
    - [x] 회원가입 요청(POST) 받기
      - [x] `http://localhost:8008/register` POST 요청시 회원가입 처리를 한다.
    - [x] 회원가입 요청 처리
      - [x] 회원가입 성공시 index.html 로 리다이렉트한다.
      - [x] 회원가입 실패시 ExistUserException 예외를 던진다.


- [x] Cookie에 JSESSIONID 값 저장하기
  - [x] 로그인 성공시에 `JSESSIONID` 쿠키를 생성해서 응답(Set-Cookie)에 함께 보낸다.
  - [x] 만약 HTTP 요청 헤더의 Cookie에 `JSESSIONID` 가 존재하면 Set-Cookie 응답을 보내지 않는다.


- [x] Session 구현하기
  - [x] 쿠키에서 전달 받은 JSESSIONID 의 값으로 로그인 여부를 체크할 수 있다.
  - [x] Session을 통해 유저 정보를 저장한다.
  - [x] 세션을 통해 로그인 여부를 판별하고 이미 로그인 된 사용자는 index.html 로 리다이렉트한다.
 
## 새롭게 알게 된 내용 (2단계)

### Content-Length 헤더 필드

큰 의미가 없다고 생각했던 `Content-Length` 헤더 필드에 대해서 직접 POST 요청의 바디 부분을 읽으면서 그 사용용도를 알게 되었다.
`Content-Length` 는 수신자에게 보내지는 바이트 단위를 가지는 요청 바디(Request Body)의 크기를 나타낸다.
수신자쪽에서는 해당 바이트의 크기만큼 읽어들여 처리할 수 있다.

```java
final int contentLength = Integer.parseInt(contentLengthHeader.trim());
final char[] buffer = new char[contentLength];
bufferedReader.read(buffer, 0, contentLength);

return new String(buffer);
```

참고 : [Content-Length - HTTP](https://developer.mozilla.org/ko/docs/Web/HTTP/Headers/Content-Length)

### InMemoryDB rollback

회원가입을 구현하고, 이를 테스트하기 위해 단위 테스트(`RegisterHandlerTest`) 를 작성하다보니 각 단위테스트가 격리되지 못하는 문제가 발생하였다.
앞선 단위 테스트에서 저장한 `User` 가 다른 단위테스트에도 영향을 끼치는 것이다.

<img width="1622" alt="KakaoTalk_Photo_2022-09-06-06-51-11" src="https://user-images.githubusercontent.com/57028386/188536783-314637b5-2b9d-460f-9e71-7e45fafbf42a.png">

따라서 이를 격리해줄 필요성이 있게 되었고, 다음과 같은 메소드를 `InMemoryUserRepositroy` 에 만들고,
`BeforeEach` 를 통해서 각 단위 테스트 진행 전에 호출하도록 구현해주었다.
하지만 아직 테스트를 위해서 Production 코드에 `rollback()` 과 같은 메소드를 만들어 두는 것이 최선인지에 대해서는 고민이든다.
조금 더 나은 방법을 고민해보아야겠다.

```java
public static void rollback() {
    database.clear();
    final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
    database.put(user.getAccount(), user);
}
```

```java
class RegisterHandlerTest {

  @BeforeEach
  void setUp() {
    InMemoryUserRepository.rollback();
  }
    
    ...
}
```

### 쿠키와 세션

쿠키와 세션을 통한 인증&인가 과정을 이론으로만 알고 있다가 실제로 구현해보니 생각보니 어려웠다.
머릿속에 있는 내용이 코드로 잘 안옮겨지는 느낌이었다.
또한 세션 저장소를 통해서 별도로 세션에 대한 관리가 필요하다는 점이 실제로 구현을 진행해보니 JWT에 비해서 불편하다는 생각도 들었다.

---

## 3단계 구현 내용 정리

- [x] HttpRequest 클래스 구현하기
  - [x] `HttpMethod`, `HttpVersion`, `HttpRequestPath` 각각의 책임을 가지는 객체로 분리
  - [x] 위의 3개의 클래스를 포함하는 `StartLine` (HTTP 요청 메시지의 첫줄) 클래스 도출
  - [x] HTTP 요청 헤더에 해당하는 `HttpHeader`
  - [x] HTTP 요청 바디에 해당하는 `HttpRequestBody`
  - [x] HTTP 요청 메시지 전체에 해당하는 `HttpRequest`
  - [x] `HttpRequest`는 `InputStream` 을 통해서 응답 메시지를 만들어내는 책임 또한 가진다.
- [x] HttpResponse 클래스 구현하기
  - [x] 상태 코드와 메시지를 합친 `StatusCode`
  - [x] 현재 응답을 보낼 때 사용하는 `Content-Type` 헤더와 `Location` 헤더 별도의 클래스로 분리
  - [x] `OutputStream` 을 받아서 `ResponsePrinter` 를 가지도록 리팩터링
  - [x] `ResponsePrinter` 를 통해서 `HttpResponse` 내에서 HTTP 응답 메시지 출력
- [x] Controller 인터페이스 추가하기
  - [x] HTTP 요청 및 응답 처리에 대한 책임을 가지는 `Controller` 도출
  - [x] 각 요청에 맞게 Controller 구현체로 매핑해줄 `RequestMapping` 구현
  - [x] 요청 처리 중 예외가 발생할 시에 해당 예외에 대한 처리를 해줄 `ControllerAdvice` 구현
- [x] 예외 처리 추가
  - [x] 사용자의 잘못된 값을 포함한 요청, 존재하지 않는 리소스 요청이나 서버 내부 에러(400, 404, 500)와 같은 에외 처리를 추가 

## 리팩터링하며 고민한 내용 (3단계)

### Request 와 Response

`HttpRequest` 와 `HttpResponse` 각각에 대해서 책임을 어떻게 하면 가장 적절하게 분배할 수 있을지를 고민하였다.
(어떻게 객체들을 구성할지 고민하였다.)

![](../../../Desktop/스크린샷 2022-09-08 14.47.42.png)

![](../../../Desktop/스크린샷 2022-09-08 14.47.45.png)

위의 그림을 참고하여 클래스를 도출해내었다.

먼저 HttpRequest에 대해서는 HTTP Method 와 요청이 온 Request Path, 그리고 HttpVersion 으로 먼저 나누었고,
이 3개의 클래스를 모두 HTTP 요청 메시지의 첫 줄,
즉 StartLine 에 해당하므로 StartLine 클래스를 도출해서 해당 클래스가 위 3개의 클래스를 필드로 가지도록 구현하였다.
그런데 이 때, QueryParam 이 함께 Path 에 붙어서 올 수 있으므로 `QueryParams` 또한 가질 수 있도록 해주었다.

그리고 `HttpRequest` 는 크게 헤더와 바디, 시작줄을 가지는데, 이 때 미션의 경우 쿠키에 대해서 비중 있게 다루고 있으므로 `Cookies` 필드 또한 가질 수 있도록 하였다.

```java
private final StartLine startLine;
private final HttpHeader headers;
private final Cookies cookies;
private final HttpRequestBody requestBody;
```

다음으로는 HTTP 응답인데, 위의 그림에서 `Version of the protocol` 에 대한 부분은 우리가 다루고 있지 않아 별도의 클래스로 분리해내지 않았다.
또한 Status Code 와 Status Message 의 경우 함께 사용되므로 하나의 `StatusCode` 로 분리해내었다.
그리고 응답 헤더의 모든 부분을 다루지 않고, 우리 미션에서 비중있게 사용되고 있는 Content-Type 과 Location에 대해서만 별도의 책임을 가지는 클래스로 도출해내었다.

### Controller

컨트롤러를 도출해내면서 가장 많은 부분이 바뀌었다.
가장 먼저 Controller 인터페이스와 AbstractController 를 다음과 같이 구현하였다.

```java
public interface Controller {
    void service(HttpRequest request, HttpResponse response) throws Exception;
}
```

```java
public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        final HttpMethod method = request.getRequestMethod();

        if (method.equals(POST)) {
            doPost(request, response);
        }
        if (method.equals(GET)) {
            doGet(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse httpResponse) throws Exception {
        throw new UnsupportedOperationException();
    }
    protected void doGet(HttpRequest request, HttpResponse httpResponse) throws Exception {
        throw new UnsupportedOperationException();
    }
}
```

위와 같이 컨트롤러를 구현하고, 실제 Controller 구현체로 매핑이 될 책임은 `RequestMapping` 으로 위임하였다.
`RequestMapping` 클래스에서는 요청이 온 URI 를 통해서 적절한 Controller를 반환해주게 되고,
`Http11Processor` 에서는 `RequestMapping` 으로 부터 반환되어온 Controller 구현체의 service 메소드를 호출하여 요청을 처리하게 된다.

처음에는 실제 요청을 HTTP Method(GET or POST) 에 따라 처리하는 `service()` 가 `request` 를 받고, `response` 를 반환하는 식으로 먼저 리팩토링하였다.
하지만 이렇게 되면 `Http11Processor` 로 부터 모든 요청과 응답에 대한 책임이 Controller로 위임되지 않는다.
`Http11Processor` 는 컨트롤러로부터 다시 응답을 받아 이를 처리해주어야한다. 이는 응답쪽의 책임이 완전히 분리되지 않는다.
또한 실제 `package javax.servlet.http.HttpServlet` 의 구조를 보면 위의 예시 코드와 같이 request, response 를 함께 받는다.
따라서 request와 response 를 모두 넘기고 response가 직접 HTTP 메시지를 만들어 클라이언트 측으로 전송해줄 수 있도록 리팩토링하게 되었다.
Response는 OutputStream을 받아 생성되고, `service()` 메소드가 종료되기 직전에 Response 객체의 OutputStream을 통해 메시지를 클라이언트측으로 전송하게 된다.
하지만 이 모든 것이 `HttpResponse` 의 책임은 아니기 때문에 `ResponsePrinter` 로 별도로 분리해내었다.

### 예외 처리

이번 3단계 리팩터링을 진행하며 예외처리에 대한 부분을 추가로 구현해주었다.

```java
    private static void handleRequest(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        try {
            final Controller controller = RequestMapping.getController(httpRequest);
            controller.service(httpRequest, httpResponse);
        } catch(Exception e) {
            ControllerAdvice.handle(httpResponse, e);
        }
    }
```

`Http11Processor` 에서 Controller 로 요청과 응답에 대한 책임을 위임하면서 발생한 예외를 `ControllerAdvice` 에서 처리해주도록 구현하였다.
이 과정에서 handle() 메소드 내부에서는 파라미터로 넘겨져온 `Exception` 의 `instanceOf` 를 활용해 분기하여 적절한 처리를 해주고 있다.
하지만 [instanceof의 사용을 지양하자](https://tecoble.techcourse.co.kr/post/2021-04-26-instanceof/) 는 글의 내용처럼 개선이 필요해보인다.

### 리팩터링하며 느낀점

이번에 리팩터링을 진행하면서 정말 작은 단위로 나눠서 리팩터링을 진행했다고 생각했음에도 불구하고, 변경이 계속해서 전파되는 느낌을 받았다.
`Request` 나 `Response` 쪽을 리팩터링할 때에는 괜찮았는데, Controller 를 도출해내면서 기존의 `LoginHandler` 와 같은 핸들러를 제거해야했으며,
`Http11Processor` 에 너무 많은 책임이 있어, 한 step씩 리팩터링하기도 쉽지 않았다.
또한 머리속에서는 미리 앞서 나가 `package javax.servlet.http.HttpServlet` 와 같은 구조로 Controller를 구현하려고 하니 리팩터링 과정이 막막하게 다가오기도 하였다.
또한 내가 지금 버그를 만들지 않으면서 구조를 개선하고 있는지 확인받기 위한 test 코드도 프로덕션 코드와 함께 깨져 피드백도 제대로 받지 못하는 상태에서 리팩터링을 진행하였다.
이러한 과정에서 느낀점은 최대한 구현을 하면서 최소한의 리팩터링을 함께 진행하자는 점과 이렇게 구조 자체를 변경해야하는 큰 단위의 리팩터링의 경우,
기존 코드는 살려두고 새로운 .java 파일을 생성해서 기존의 테스트나 프로덕션 코드는 제대로 돌아가게 두고 하나씩 교체하는 방향으로 리팩터링 하는 것도 하나의 방법이 될 수 있겠다 이다. 

---

## 4단계 구현 내용 정리

- [x] Executors로 Thread Pool 적용
  - [x] `newFixedThreadPool` 을 사용하여 고정된 개수(250)의 쓰레드를 재사용하며 초과되는 요청은 대기 상태(큐)
- [x] 동시성 컬렉션 사용하기
  - [x] `Sessionmanager`에 `ConcurrentHashMap` 을 사용하여 Session 컬렉션에 대해 쓰레드 안정성을 보장

## 새롭게 알게 된 내용 (4단계)

### newCachedThreadPool vs newFixThreadPool

`newFixedThreadPool`은 공유 언바운드 큐에서 작동하는 고정된 수의 쓰레드를 재사용하는 쓰레드풀을 생성한다.
만약 동시에 실행되는 태스크의 수가 최대 쓰레드의 수를 초과한다면 작업 중 일부를 큐에 넣어 순서를 기다리게 된다.
반면 `newCachedThreadPool` 은 사용가능한 쓰레드가 없다면 새롭게 쓰레드를 생성한다.

### submit() vs execute()

두 메소드 모두 쓰레드풀에게 작업 처리를 요청하는 메소드이다.
`execute()` 로 실행했을 때는 작업 처리 도중 예외가 발생하면 해당 쓰레드는 제거되고 새 쓰레드가 계속해서 생겨난다.
반면 `submit()` 의 경우에는 예외가 발생하더라도 쓰레드가 종료되지 않고 계속해서 재사용되어 다른 작업을 처리할 수 있다.
따라서 쓰레드 생성의 오베헤드를 줄일 수 있다.
또한 `submit()` 은 작업 처리 결과를 받을 수 있도록 Future를 리턴하는데, Future는 작업 실행 결과나 작업 상태(실행중)를 확인하기 위한 객체이다.

### ExecutorService의 shutdown()

`ExecutorService`의 `shutdown()` 메서드는 실행자 서비스를 즉시 종료시키지 않는다.
작업큐에 남아있는 작업까지 모두 마무리한 후 종료한다.
이와 다르게 `shutdownNow()` 메소드는 작업큐의 작업 잔량과 관계없이 강제 종료시킨다.
(The shutdown() method doesn't cause immediate destruction of the ExecutorService.
It will make the ExecutorService stop accepting new tasks and shut down after all running threads finish their current work)

### ConCurrentHashMap

`ConcurrentHashMap` 은 `put()` 메소드에 대해서 `synchronized` 를 붙여,
읽기에 대해서는 여러 쓰레드에서 동시에 읽을 수 있도록 하지만, 쓰기에 있어서는 동시에 하나의 쓰레드만 접근이 가능하도록 한다.
