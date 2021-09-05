## 🚀 1단계 - HTTP 서버 구현하기
- [x] http request 를 파싱하기
  - [x] StringUtils 만들기
- [x] http response 만들기
- [x] 정적 요청 관련 매핑
  - [x] html 요청 관련 매핑
  - [x] css 요청 관련 매핑
  - [x] js 요청 관련 매핑
- [x] forward 요청 관련 매핑
- [x] redirect 요청 관련 매핑
- [x] 매핑이 없으면 404를 응답한다.
- [x] 인증에 실패하면 401을 응답한다.
- [x] POST 구현
  - [x] 회원가입 구현
  - [x] login POST로 리팩터링
  

## 🍪 3단계 쿠키, 세션 구현하기
- [ ] 요청에 쿠키가 들어오면 HttpRequest 에서 쿠키를 얻어내는 기능 구현하기
  - [x] HttpCookies 객체 만들기
  - [ ] RequestHeader 에 쿠키를 추출하는 메서드 만들기
