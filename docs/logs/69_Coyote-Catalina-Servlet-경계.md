# 69. Coyote·Catalina·Servlet — HTTP 바이트에서 애플리케이션까지

**날짜**: 2026-09-27
**학습 범위**: java-http Step3 리뷰에서 Coyote가 Catalina의 세션 관리자를 아는 문제를 출발점으로, Tomcat 내부의 요청 표현과 Servlet 경계를 재방문
**프로젝트**: java-http ([PR #1327](https://github.com/woowacourse/java-http/pull/1327))
**재방문**: #60 Tomcat의 연결·번역과 서블릿 컨테이너, #21 DispatcherServlet 경계

분류: Spring 웹 흐름 / 아키텍처·의존성

## 출발점 — 패키지 이름보다 요청이 지나가는 순서

리뷰는 Coyote 쪽 `HttpRequest`가 Catalina의 `Manager`와 `HttpSession`을 직접 아는 구조를 짚었다. 처음에는 패키지를 어디로 옮길지가 문제처럼 보였지만, 핵심은 요청의 **낮은 수준 표현**과 **Servlet에서 사용할 표현**을 어느 지점에서 연결할지였다.

```text
소켓에서 HTTP 바이트 수신
→ Coyote: 프로토콜을 처리하고 낮은 수준의 요청·응답을 표현
→ Catalina: 그 요청을 Servlet API와 컨테이너 기능에 연결
→ Servlet/DispatcherServlet: 요청을 애플리케이션의 처리기로 전달
→ 애플리케이션: 로그인·회원가입 같은 일을 수행
```

#60에서는 Tomcat이 연결을 처리하고 서블릿을 실행한다는 큰 흐름을 배웠다. 이번에는 그 내부에서 Coyote와 Catalina가 다른 요청 표현을 다룬다는 점까지 내려갔다. 실제 Tomcat의 `org.apache.coyote.Request`는 내부용 낮은 수준의 요청 표현이고, `org.apache.catalina.connector.Request`는 이를 감싸며 `HttpServletRequest`를 구현한다. [Coyote Request](https://tomcat.apache.org/tomcat-11.0-doc/api/org/apache/coyote/Request.html) · [Catalina Request](https://tomcat.apache.org/tomcat-11.0-doc/api/org/apache/catalina/connector/Request.html)

## Servlet을 다시 이해한 지점

Servlet은 Java 서버에서 요청·응답을 처리하는 **표준 계약**이다. `HttpServletRequest`에는 `getSession(boolean)`처럼 세션을 얻는 API가 있고, 세션은 여러 HTTP 요청 사이에 사용자 정보를 이어준다. 따라서 바이트를 파싱하는 일과, 애플리케이션에 세션을 포함한 요청 인터페이스를 제공하는 일은 같은 책임이 아니다. [HttpServletRequest](https://jakarta.ee/specifications/servlet/6.1/apidocs/jakarta.servlet/jakarta/servlet/http/httpservletrequest) · [HttpSession](https://jakarta.ee/specifications/servlet/6.1/apidocs/jakarta.servlet/jakarta/servlet/http/httpsession)

학습 중 스스로 잡은 문장인 “웹 요청 표준이 Servlet”은 이 맥락에서 **Java 웹 서버의 요청 처리 표준 계약**으로 이해한다. HTTP 프로토콜 자체의 표준과 같은 말은 아니다.

## 현재 미션에 대입

- Coyote의 `HttpRequest`가 Catalina 세션 관리 구현을 직접 알면, 프로토콜 계층에서 컨테이너 책임까지 끌어안는다.
- 로그인 여부 판단과 세션에 사용자 저장은 요청을 사용하는 웹 계층의 관심사다. 세션 생성·조회 자체는 컨테이너가 제공한다.
- 애플리케이션의 라우팅 정보와 인증 동작을 어디에 둘지는 별도로 결정한다. 현대 Tomcat의 Servlet 매핑과 Spring MVC의 `@RequestMapping` 등록도 같은 단계가 아니다.

이번 대화는 경계를 이해하고 Step3 리뷰의 의미를 해석하는 데 집중했다. 완전한 Servlet 요청 래퍼와 포트 분리까지 구현한 것으로 기록하지 않는다.

## 학습 방식 회고와 다음 연결

초반의 추상적인 질문과 성급한 리팩터링 제안은 흐름을 끊었다. “소켓 바이트가 어느 요청 객체가 되어 누구에게 전달되는가”라는 순서로 보자 책임의 이유가 연결됐다. 다음에는 새 구조를 제안하기 전에 **현재 요청이 지나가는 지점과 그 지점이 아는 타입**을 먼저 확인한다.

재방문 질문: Coyote 요청 표현이 세션 관리자를 직접 알면 어떤 책임이 거꾸로 흘러오는가? Tomcat의 Catalina Request가 Coyote Request를 감싸는 이유는 무엇인가?

현재 BACKLOG의 hot arc는 컴퓨터 구조 순회이므로 새 항목은 만들지 않는다. 씨앗: `HttpRequest`와 Servlet 요청 인터페이스 사이의 경계를 실제 코드에서 어디에 둘지.
