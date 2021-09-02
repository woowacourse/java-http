# HTTP 서버 구현하기

## WEB APPLICATION SERVER
### HttpRequest 구현
- 라인, 헤더, 바디 구분해서 구현
- 각각에 데이터 구분지어 삽입
- HttpCookie 를 생성
- 모든 세션 정보를 가지는 HttpSessions 와 각 세션 객체 생성

### HttpResponse 구현
- 응답 폼 맞게 구현 (응답 코드, 헤더, 바디)
- flush() 를 호출하면 바로 폼에 맞는 응답 클라이언트에 전달

### SPRING CORE
- 빈 등록 기능 구현
- 빈 컴포넌트 스캔 기능 구현 (Reflections 사용)
- Autowired 구현

### SPRING MVC
- DispatcherServlet 구현
  - 흐름 :
    1. 필요한 모든 객체 생성 (HandlerMapping, ViewResolver, MultipartResolver)
    2. request 에 맞는 Handler 찾기 (HandlerMapping)
    3. Handler 가 존재하지 않다면 static resource 확인
    4. static Resource 도 존재하지 않다면 404 에러를 담아주기
    5. Handler 가 존재한다면 실행 이후 ModelAndView 리턴
    6. ModelAndView 를 가지고 ViewResolver 실행
    7. 위 과정에서 오류가 생긴다면 ErrorResolver 실행
  - 디테일 :
    - HandlerMapping
      - 클래스형 핸들러와 메서드형 핸들러(어노테이션 기반)를 나눈다.
      - 메서드형 핸들러에는 @Controller 기반으로 메서드를 모두 분리해서 해당 클래스에 등록한다.
      - 메서드형 핸들러에는 ArgumentResolver 와 ReturnValueResolver 를 추가해 편의성을 더해준다.
    - ViewResolver
      - view 가 존재한다면 response 에 뷰를 렌더링해서 데이터를 내보낸다.

### 테스트
- 요청 응답 테스트 도구 구현

## 해야할 것들 (하고싶은 것들)
- 전체적인 리팩토링 (구조 다시 잡기)
- Argument Resolver 구현
- Controller 와 RestController 구분
- Converter 기능 구현 (테스트에도 필요)
