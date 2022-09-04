# 톰캣 구현하기

## 기능 요구사항

### 1. HTTP 서버 구현하기
- [x] GET /index.html 응답하기
- [x] CSS 지원하기
  - [x] 요청에 따라 다른 정적 파일 위치를 찾도록 수정
  - [x] 요청에 따라 컨텐츠 타입 찾도록 수정
- [x] Query String 파싱
  - [x] /login 페이지 처리하기
  - [x] Query String이 일치할 경우 로그를 찍어준다.

### 테스트 보완하기
- [x] 테스트를 보완한다.
  - [x] CSS 지원 테스트
  - [x] js 지원 테스트
  - [x] 404 페이지 테스트 ( + NotFound 응답 메세지 )
  - [x] HttpRequest 잘 만들어지는지 테스트

### 예외 처리하기
- [x] param에 값이 없는 경우 처리하기 (account=&passowrd=123)
- [x] 현재 레포지터리에 없는 값으로 로그인 했을 경우 NoSuchElementException을 띄우는데 이를 처리하기
- [ ] 커스텀 예외를 만들어 Http 관련 예외 처리하기

### 리팩토링
- [x] Http11Processor에서 Parsing하는 책임 분리하기
- [x] 비즈니스 로직과 tomcat 기능을 분리하기
- [x] HttpRequest에서 일급컬렉션 사용하기
- [x] 반복되는 상수를 Enum으로 처리하기
- [ ] loginController 악취 제거하기
- [ ] RequestMapping - service 과정까지에서 예외를 어떻게 처리할 것인지 고민해보기 
