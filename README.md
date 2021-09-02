# HTTP 서버 구현하기

## 기능 요구사항 및 테스트 요구사항
### step 1
- [x] File 클래스 학습 테스트
- [x] Java I/O Stream 클래스 학습 테스트
- [x] RequestHandlerTest
- [x] GET /index.html 응답하기
- [x] Query String 파싱
- [x] HTTP Status Code 302
- [x] POST 방식으로 회원가입
- [x] CSS 지원하기

### step 3
- [x] 쿠키 구현
  - [x] UUID를 활용하여 고유한 세션 ID를 생성한다.
  - [x] 로그인에 성공하면 Set-Cookie 헤더를 이용하여 세션 ID를 전달한다.
- [ ] 세션 구현
  - [x] JSESSION 값으로 로그인 여부를 체크한다.
  - [x] 로그인 성공할 경우 HttpSession 객체의 값을 이용하여 User 객체를 저장한다.
  - [ ] 로그인 상태에서 /login 에 GET 메서드로 접근하면 index.html로 리다이렉트한다.