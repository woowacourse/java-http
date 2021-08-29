# HTTP 서버 구현하기

## WEB APPLICATION SERVER
### HttpRequest 구현
- 라인, 헤더, 바디 구분해서 구현
- 각각에 데이터 구분지어 삽입

### HttpResponse 구현
- 응답 폼 맞게 구현 (응답 코드, 헤더, 바디)

### SPRING CORE
- 빈 등록 기능 구현
- 빈 컴포넌트 스캔 기능 구현 (Reflections 사용)
- Autowired 구현

### SPRING MVC
- 프론트 핸들러 구현
- 핸들러 매핑 구현 (추후 핸들러 체이닝 구현 필요)
- 핸들러 어댑터 방식으로 메서드 핸들러 연결
- 메서드 핸들러 구현

### 테스트
- 요청 응답 테스트 도구 구현

## 해야할 것들 (하고싶은 것들)
- 전체적인 리팩토링 (구조 다시 잡기)
- Argument Resolver 구현
- Controller 와 RestController 구분
- Converter 기능 구현 (테스트에도 필요)
