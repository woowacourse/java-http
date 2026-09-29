# 70. @Controller·@RequestMapping — 메타데이터가 실행 가능한 라우트가 되기까지

**날짜**: 2026-09-27
**학습 범위**: java-http Step3 로그인 컨트롤러 응집도 리뷰를 어노테이션 기반 라우팅으로 반영하며, 발견·등록·호출을 연결
**프로젝트**: java-http ([PR #1327](https://github.com/woowacourse/java-http/pull/1327))
**재방문**: #01 `@RestController`와 응답 본문, #21 DispatcherServlet 경계, #62 프레임워크가 어노테이션 메서드를 발견해 호출하는 원리

분류: Spring 웹 흐름 / 아키텍처·의존성

## 리뷰에서 출발한 설계 기준

[리뷰 #4](https://github.com/woowacourse/java-http/pull/1327#discussion_r4110596960)는 `GET /login`과 `POST /login`을 서로 다른 클래스로 나누면서 `GetLoginController`가 `PostLoginController.LOGIN_USER`를 참조하게 된 점을 짚었다. 세션 키만 별도 클래스로 빼면 직접 참조는 사라지지만 로그인 처리 자체의 분산은 남는다.

선택한 방향은 **로그인 처리를 한 `LoginController`에 모으고, GET/POST 매핑 정보는 각 메서드 옆에 유지**하는 것이다. `RouteKey`가 HTTP 메서드와 경로를 함께 표현하는 장점도 살린다. 회원가입도 같은 방식으로 한 클래스에 GET/POST 메서드를 둔다.

## 시작 시 등록되는 순서

```text
@Controller가 붙은 클래스 발견
→ 컨트롤러 객체 준비
→ 객체의 메서드 중 @RequestMapping이 붙은 것만 선택
→ 어노테이션의 HTTP 메서드·경로로 RouteKey 생성
→ 그 객체의 그 Method를 실행하는 RequestHandler 등록
→ 요청 시 Dispatcher가 RouteKey로 RequestHandler를 찾아 실행
```

어노테이션은 표시만 남긴다. `@RequestMapping`이 스스로 맵을 채우거나 `@Controller`가 스스로 객체를 만들지는 않는다. Java에서 `@Target(ElementType.METHOD)`는 메서드용, `@Target(ElementType.TYPE)`은 클래스용이다. 실행 중 어노테이션을 읽으려면 `@Retention(RetentionPolicy.RUNTIME)`이 필요하다. [Oracle 어노테이션 문서](https://docs.oracle.com/javase/tutorial/java/annotations/predefined.html)

`Class.getDeclaredMethods()`는 어노테이션이 아니라 **그 클래스에 선언된 메서드 목록**을 준다. 각 `Method`에서 `getAnnotation(RequestMapping.class)`으로 원하는 표시만 확인한다. `Method.invoke(controllerObject, request, response)`의 첫 인자는 실행 대상 **객체**, 뒤의 인자는 메서드에 건넬 값이다. [Class API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Class.html) · [Method API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/reflect/Method.html)

## 이번에 연결된 핵심 — 클래스와 실행 함수를 구분

처음에는 Registry의 `Map<RouteKey, Controller>`를 `Method` 맵으로 바꿔야 한다고 생각했다. 그러나 `Method` 하나만으로는 어느 객체에서 실행할지 알 수 없다. 기존 함수형 인터페이스를 `RequestHandler`로 이름 짓고, 등록 시 람다가 **컨트롤러 객체와 Method를 함께 기억**하도록 하면 된다.

```text
GET  /login → RequestHandler(controller=같은 LoginController, method=getLoginPage)
POST /login → RequestHandler(controller=같은 LoginController, method=handle)
```

따라서 Registry는 `Map<RouteKey, RequestHandler>`를 유지한다. `LoginController`는 `RequestHandler`를 구현할 필요가 없다. 기존 `Controller` 인터페이스를 `RequestHandler`로, 기존 `RequestMapping` 저장소를 `RequestRegistry`로 바꿔 어노테이션과 실행 함수의 이름 충돌도 풀었다. 이 구분이 보였을 때 “생각도 못했다”는 반응이 나왔다.

## @Controller는 무엇을 더하는가

`WebConfig`에 객체를 직접 나열하면 `@Controller`는 없어도 된다. 직접 등록을 줄이려면 패키지 범위를 정해 `@Controller` 클래스를 찾는 **클래스패스 탐색**이 별도로 필요하다. 프로젝트의 Reflections 의존성은 `getTypesAnnotatedWith()`로 `Set<Class<?>>`를 얻는다. 이 결과는 컨트롤러 **객체가 아니라 클래스**이므로, 객체를 만든 뒤 메서드 등록 단계에 넘겨야 한다. [Reflections README](https://github.com/ronmamo/reflections)

**현재 확인할 연결**: `WebConfig.getControllers()`는 탐색 결과의 `Class<?>`들을 `List<Object>`로 옮기지만 인스턴스를 생성하지 않는다. 이어서 `handler.getClass().getDeclaredMethods()`를 호출하면 컨트롤러가 아니라 `java.lang.Class`의 메서드를 살펴보게 된다. 클래스 발견과 객체 생성은 아직 이어야 한다. 앞서 `:tomcat:test`는 명시적 테스트 라우트로 통과했지만, 그 결과가 이 새 자동 탐색 경로까지 증명하지는 않는다.

## Spring 원본과 비교

Spring은 컴포넌트 스캔으로 클래스를 찾아 Bean으로 등록한 뒤, MVC의 `RequestMappingHandlerMapping`이 Bean의 `@Controller` 여부를 확인하고 `@RequestMapping` 메서드를 등록한다. 이 둘을 나누는 점은 이번 축약 구현과 같다. Spring의 `@RestController`는 `@Controller`에 `@ResponseBody` 의미를 더한 것으로, 별도의 `HandlerAdapter`를 고르는 표시가 아니라 **반환값을 본문으로 처리하는 규칙**에 영향을 준다. [Spring 컨트롤러 탐색](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann.html) · [Spring 메서드 등록 구현](https://github.com/spring-projects/spring-framework/blob/main/spring-webmvc/src/main/java/org/springframework/web/servlet/handler/AbstractHandlerMethodMapping.java) · [Spring @ResponseBody](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/responsebody.html)

## 학습 방식 회고와 다음 연결

“어느 컨트롤러·어느 메서드인가”까지는 스스로 찾았지만, `invoke()` 문법을 몰라 구현 그림이 멈췄다. 이때 추상 질문을 더하기보다 `Method.invoke(대상 객체, 인자...)` 한 줄과 Registry의 실제 타입을 보여주자 연결됐다. 다음에는 **개념을 이미 세웠는데 문법만 모를 때 구문을 즉시 제공**한다. 질문은 답을 통해 바뀔 결정이 분명할 때만 한다.

재방문 질문: `getTypesAnnotatedWith()`가 돌려주는 것과 `getDeclaredMethods()`가 돌려주는 것은 각각 무엇인가? Registry가 `Method`만 저장하면 왜 부족한가? `@RestController`가 반환값 처리에 영향을 주는 이유는 무엇인가?

현재 BACKLOG의 hot arc는 컴퓨터 구조 순회이므로 새 항목은 만들지 않는다. 씨앗: 발견한 `Class<?>`의 객체 생성과 생성자 의존성, 자동 등록 경로를 통과하는 테스트.
