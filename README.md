# 톰캣 구현하기

## 요구사항
### 1단계 - HTTP 서버 구현하기
-[x] GET /index.html 응답하기
-[x] CSS 지원하기
-[x] Query String 파싱

### 2단계 - 로그인 구현하기
-[x] HTTP Status Code 302
-[x] POST 방식으로 회원가입
-[x] Cookie에 JSESSIONID 값 저장하기
-[x] Session 구현하기

## 3단계 - 리팩터링
-[x] HttpRequest 클래스 구현
-[x] HttpResponse 클래스 구현
-[x] Controller 인터페이스 구현
-[x] AbstractController 추상 클래스 구현
-[x] RequestMapping 클래스 구현

### TODO
-[x] 예외 처리
-[x] 매직 넘버 정리
-[x] 리팩터링(클래스 분리, 패키지 정리)
-[x] 상태코드, http 메서드 종류 추가 
-[ ] 테스트 - 회원가입, 정적리소스
-[ ] 로깅
-[ ] 예외 처리
-[ ] 캐싱 구현해보기
