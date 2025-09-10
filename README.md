# 만들면서 배우는 스프링

## 톰캣 구현하기 - 1단계

### 학습목표

- 웹 서버 구현을 통해 HTTP 이해도를 높인다.
- HTTP의 이해도를 높여 성능 개선할 부분을 찾고 적용할 역량을 쌓는다.
- 서블릿에 대한 이해도를 높인다.
- 스레드, 스레드풀을 적용해보고 동시성 처리를 경험한다.

### 구현 구조

- 톰캣의 구조를 단순화해 구현한다.
- Coyote의 요청을 적절한 핸들러로 전달하는 기능만 구현하여, 서블릿 관련 기능 단순화한다.
- 필터, 인터셉터 등의 전후 처리는 현재 미션에서 제외한다.

```
HTTP 요청
↓
[Coyote] - HTTP 커넥터
↓  
[Catalina] - Servlet 컨테이너
↓
[HandlerDispatcher] - 적절한 핸들러로 요청 전달
↓
↓ - 정적 파일 요청을 처리하는 핸들러 -> StaticResourceHandler
↓ - 비즈니스 요청을 처리하는 핸들러 -> ControllerHandler
↓
[HandlerMapping] - RequestLine에 따라 적절한 컨트롤러 매핑
↓
↓
[Controller] - 비즈니스 로직 (앱단)
↓
[ViewResolver] - Controller 이후 응답을 위한 Html 파일 저장
↓
HTTP 응답
```
