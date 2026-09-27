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

- [x] URI와 Controller 등록을 `Http11Processor`에서 `RequestMapping.forSession()`으로 옮긴다. 새 URI를 추가할 때 Processor를 수정하지 않고, 요청별
  `Session`을 로그인 Controller에 전달한다.
- [x] `HttpResponse`의 응답 생성 방식을 검토한다. 현재는 상태별 메서드가 동작을 읽기 쉽게 보여주므로 유지한다. 상태 코드나 헤더 조합이 늘어나 중복 변경이 반복되면 공통 작성 메서드를 추출한다.
- [x] 현재 사용하지 않는 `Session.removeAttribute()`와 `Session.invalidate()`를 제거한다. 필요해질 때 실제 사용 사례와 함께 추가한다.
