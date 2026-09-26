# 3단계 - 리팩터링

## 완료 조건

### HTTP 요청

- [x] HTTP 요청 처리 책임을 `HttpRequest`와 관련 객체로 분리한다.
- [x] 리팩터링 후에도 기존 HTTP 요청 해석 동작을 유지한다.

### HTTP 응답

- [x] HTTP 응답 처리 책임을 `HttpResponse`와 관련 객체로 분리한다.
- [x] 리팩터링 후에도 기존 HTTP 응답 동작을 유지한다.

### Controller

- [ ] `Controller` 인터페이스를 도입한다.
- [ ] HTTP Method에 따른 처리를 `AbstractController`에서 분기한다.
- [ ] 요청 경로에 따른 애플리케이션 처리를 각 Controller로 분리한다.
- [ ] WAS와 HTTP 처리 코드가 로그인, 회원가입 등의 애플리케이션 로직을 직접 처리하지 않는다.
