# 톰캣 구현하기

## 개요

이 프로젝트는 Spring Boot의 내장 톰캣 서버에서 HTTP 요청이 어떻게 처리되는지 이해하기 위해 톰캣의 핵심 구조를 간소화하여 구현한 버전입니다.

**주요 관심사**: 톰캣의 HTTP 요청 처리 메커니즘을 학습하는 것이 목적이므로, 운영 환경에서 필요한 고급 기능들(필터 체인, 보안 처리, 클러스터링 등)은 학습 목적상 의도적으로 배제하고 순수 톰캣 구조에
집중하여 구현했습니다.

### 학습목표

- 웹 서버 구현을 통해 HTTP 이해도를 높인다.
- HTTP의 이해도를 높여 성능 개선할 부분을 찾고 적용할 역량을 쌓는다.
- 서블릿에 대한 이해도
- 높인다.
- 스레드, 스레드풀을 적용해보고 동시성 처리를 경험한다.

### 구현 구조

- 톰캣의 구조를 단순화해 구현한다.
- Coyote의 요청을 적절한 핸들러로 전달하는 기능만 구현하여, 서블릿 관련 기능 단순화한다.
- 필터, 인터셉터 등의 전후 처리는 현재 미션에서 제외한다.

## 전체 아키텍처

```
HTTP 요청
    ↓
┌─────────────────────────────────┐
│ [Tomcat]                        │ - 웹 애플리케이션 서버 시작점
│ org.apache.catalina.startup     │
└─────────────────────────────────┘
    ↓
┌─────────────────────────────────┐
│ [Connector]                     │ - HTTP 커넥터 (클라이언트 연결 수락)
│ org.apache.catalina.connector   │ - ServerSocket + ExecutorService
└─────────────────────────────────┘
    ↓
┌─────────────────────────────────┐
│ [Http11Processor]               │ - HTTP/1.1 프로토콜 처리 (Coyote 엔진)
│ org.apache.coyote.http11        │ - RequestLine, Headers 파싱
└─────────────────────────────────┘
    ↓
┌─────────────────────────────────┐
│ [CoyoteAdapter]                 │ - 프로토콜 엔진과 Servlet 컨테이너 연결 어댑터
│ org.apache.catalina.connector   │ - HTTP 요청/응답 변환
└─────────────────────────────────┘
    ↓
┌─────────────────────────────────┐
│ [CatalinaContainer]             │ - 적절한 핸들러로 요청 전달 (Catalina)
│ org.apache.catalina.core        │ - 정적/동적 요청 분기
└─────────────────────────────────┘
    ↓
┌──────────────────┬──────────────────┐
│ 정적 파일 요청    │ 비즈니스 요청     │
│ (.css, .js 등)   │ (동적 처리)       │
└──────────────────┴──────────────────┘
    ↓                     ↓
┌─────────────────┐ ┌───────────────────┐
│[StaticResource  │ │[ControllerHandler]│
│RequestHandler]  │ │                   │
└─────────────────┘ └───────────────────┘
    ↓                     ↓
   HTTP 응답        [RequestHandlerMapper] - URL에 따라 적절한 핸들러 매핑
                          ↓
                    [Controller] - 비즈니스 로직 처리 (애플리케이션 계층)
                          ↓
                    [ViewResolver] - Controller 응답 후 HTML 파일 로드
                          ↓
                    HTTP 응답
```

## 핵심 컴포넌트 설명

### 1. Tomcat (톰캣 메인)

- **파일**: `org.apache.catalina.startup.Tomcat`
- **역할**: 웹 애플리케이션 서버의 시작점

### 2. Connector (커넥터)

- **파일**: `org.apache.catalina.connector.Connector`
- **역할**: HTTP 클라이언트 연결을 수락하고 소켓 통신 관리
- 동기 I/O 방식으로 단순화 (실제는 비동기 NIO)

### 3. Http11Processor (HTTP 프로세서)

- **파일**: `org.apache.coyote.http11.Http11Processor`
- **역할**: HTTP/1.1 프로토콜 파싱 및 요청/응답 처리
- **Coyote 엔진**: 톰캣의 HTTP 커넥터 구현체
- 기본 HTTP 파싱만 지원 (Keep-Alive, HTTP/2 등 미지원)

### 4. CoyoteAdapter (코요테 어댑터)

- **파일**: `org.apache.catalina.connector.CoyoteAdapter`
- **역할**: Coyote(프로토콜 엔진)와 Catalina(서블릿 컨테이너) 간의 브리지
- 실제 톰캣의 어댑터 패턴과 동일한 역할

### 5. CatalinaContainer (카탈리나 컨테이너)

- **파일**: `org.apache.catalina.core.CatalinaContainer`
- **역할**: 요청 유형에 따라 적절한 핸들러로 라우팅하는 메인 컨테이너
- 실제 톰캣의 Engine/Host/Context/Wrapper 계층을 단일 컨테이너로 통합

### 6. Request Handlers (요청 핸들러들)

#### StaticResourceRequestHandler (정적 리소스 핸들러)

- **파일**: `org.apache.catalina.handler.StaticResourceRequestHandler`
- **역할**: CSS, JS, 이미지 등 정적 파일 요청 처리
- 기본 파일 서빙만 지원 (캐싱, 압축 등 미지원)

#### ControllerHandler (컨트롤러 핸들러)

- **파일**: `org.apache.catalina.handler.ControllerHandler`
- **역할**: 동적 비즈니스 로직 요청을 컨트롤러로 전달

### 7. RequestHandlerMapper (요청 핸들러 매퍼)

- **파일**: `org.apache.catalina.handler.RequestHandlerMapper`
- **역할**: URL 패턴에 따라 적절한 핸들러 매핑

### 8. Controller (컨트롤러)

- **파일**: `com.techcourse.controller.*` (HomeController, LoginController 등)
- **역할**: 실제 비즈니스 로직 처리

### 9. ViewResolver (뷰 리졸버)

- **파일**: `org.apache.catalina.web.resolver.ViewResolver`
- **역할**: 컨트롤러 처리 후 HTML 뷰 파일 로드

### 10. SessionManager (세션 관리자)

- **파일**: `org.apache.catalina.session.SessionManager`
- **역할**: JSESSIONID 쿠키를 통한 세션 추적 및 관리

## 요청 처리 플로우

### 1. 정적 파일 요청 예시 (`/static/style.css`)

```
HTTP 요청 → Tomcat → Connector → Http11Processor → CoyoteAdapter 
→ CatalinaContainer → StaticResourceRequestHandler → HTTP 응답
```

### 2. 동적 요청 예시 (`/login`)

```
HTTP 요청 → Tomcat → Connector → Http11Processor → CoyoteAdapter 
→ CatalinaContainer → ControllerHandler → RequestHandlerMapper 
→ LoginController → ViewResolver → HTTP 응답
```
