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

<details>
<summary>3단계 · HTTP 요청·응답과 컨트롤러 분리</summary>

## 3단계 구현 요구사항

기존 로그인, 회원가입, 세션, 정적 파일 응답은 유지하면서 `Http11Processor`의 역할을 나눈다.

### HTTP 요청

- [x] `RequestLine`이 HTTP 메서드, 요청 경로, 버전을 구분한다.
- [x] `HttpRequest`가 요청의 첫 줄, 헤더, 본문을 읽고 필요한 값을 제공한다.
- [x] `Content-Length`를 바이트 수로 처리해 한글이 포함된 본문도 정확히 읽는다.
- [x] GET 쿼리 문자열과 POST 폼 본문에서 파라미터를 읽는다. 폼 인코딩된 값은 디코딩한다.
- [x] 요청 쿠키에서 `JSESSIONID`를 읽어 기존 세션을 찾을 수 있다.

### HTTP 응답

- [x] `HttpResponse`가 상태 줄, 헤더, 빈 줄, 본문 순서로 응답을 출력한다.
- [x] 정적 파일 응답의 `Content-Type`과 UTF-8 바이트 기준 `Content-Length`를 설정한다.
- [x] 리다이렉트할 때 `302`와 `Location`을, 새 세션이 필요할 때 `Set-Cookie`를 설정한다.

### 컨트롤러와 요청 매핑

- [x] `Controller`가 `service(HttpRequest, HttpResponse)` 계약을 정의한다.
- [x] `AbstractController`가 HTTP 메서드에 따라 `doGet` 또는 `doPost`를 호출한다.
- [x] 로그인 로직을 `LoginController`로 옮긴다.
- [x] 회원가입 로직을 `RegisterController`로 옮긴다.
- [x] 정적 파일 응답 로직을 `StaticResourceController`로 옮긴다.
- [x] `RequestMapping`이 요청 경로에 맞는 컨트롤러를 찾는다.
- [x] `Http11Processor`는 요청 생성, 컨트롤러 호출, 응답 출력 흐름을 연결한다.

### 테스트 작성 순서

- [x] `RequestLine`이 `GET /login HTTP/1.1`을 메서드, 경로, 버전으로 나누는 테스트
- [x] `HttpRequest`가 헤더와 `Content-Length`가 있는 POST 본문을 읽는 테스트
- [x] `HttpRequest`가 GET 쿼리와 POST 폼 파라미터를 디코딩하는 테스트
- [x] `HttpResponse`가 200 응답과 302 리다이렉트를 출력하는 테스트
- [x] `RequestMapping`과 `AbstractController`가 경로와 메서드에 맞게 호출하는 테스트
- [x] 기존 `Http11ProcessorTest`가 계속 통과하는지 확인

## 3단계 리뷰 후 리팩터링 목록

- [x] URI와 Controller 등록을 `Http11Processor`에서 분리한다. 두 번째 리뷰에서 앱 설정으로 옮겨 WAS에는 조회 역할만 남긴다.
- [x] `HttpResponse`의 상태별 메서드는 유지하고, 반복되는 상태 줄,헤더,본문 출력을 `writeResponse()`로 묶는다.
- [x] 현재 사용하지 않는 `Session.removeAttribute()`와 `Session.invalidate()`를 제거한다. 필요해질 때 실제 사용 사례와 함께 추가한다.

## 3단계 두 번째 리뷰 반영 목록

- [x] `RequestMapping`은 전달받은 경로와 Controller를 조회하는 역할만 맡는다.
- [x] 로그인, 회원가입, 기본 경로 Controller와 URI 등록을 애플리케이션 영역으로 옮긴다.
- [x] 애플리케이션에서 만든 매핑을 서버 시작 시 전달하고, 요청별 `Session`은 로그인 Controller에 전달한다.
- [x] 기존 로그인, 회원가입, 정적 파일 응답이 계속 동작하는지 확인한다.

</details>

<details open>
<summary>4단계 · 스레드 풀과 세션 동시성</summary>

## 4단계 구현 요구사항

### 스레드 풀

- [x] `Connector`에 `maxThreads` 설정을 추가한다.
- [x] 요청마다 `new Thread`를 만드는 대신 `ExecutorService`에 처리 작업을 맡긴다.
- [x] 서버를 멈출 때 스레드 풀도 종료한다.

### 세션 동시성

- [x] `SessionManager`와 `Session`의 저장소에 `ConcurrentHashMap`을 사용한다. 이전 단계에서 적용했다.
- [x] 여러 요청이 같은 세션에 접근할 때, 단일 연산과 여러 연산을 묶은 처리의 차이를 확인한다.

`ConcurrentHashMap`의 `get`, `put`은 각각 안전하지만, `get` 후 `put`까지 한 번에 처리되는 것은 아니다. 현재 세션은 UUID로 새 ID를 만들어 저장하므로 같은 키에 대한 갱신 경쟁은 없다.

### 확인할 동작

- [x] 동시에 요청이 들어와도 작업 스레드 수가 `maxThreads`를 넘지 않는지 확인한다.
- [x] 기존 HTTP 요청·응답 테스트가 계속 통과하는지 확인한다.

### 생각해보기

- `acceptCount`와 `maxThreads`는 각각 어느 대기 공간과 스레드 수를 제한할까?
- `Executors.newFixedThreadPool()`에서 모든 스레드가 바쁘면 새 작업은 어디에서 기다릴까?
- 작업 스레드 최대 250개, 대기 작업 최대 100개를 제한하려면 무엇을 설정해야 할까?

`acceptCount`는 연결을 수락하기 전 대기열 설정이고, `maxThreads`는 동시에 요청을 처리하는 작업 스레드 수다. `newFixedThreadPool()`의 작업 대기열에는 크기 제한이 없다. 대기 작업도 100개로 제한하려면 크기가 100인 큐를 사용하는 `ThreadPoolExecutor`가 필요하다.

</details>
