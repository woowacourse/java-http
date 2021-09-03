# HTTP 서버 구현하기

## 1 단계
- [x] GET /index.html 응답하기
- [x] Query String 파싱
- [x] HTTP Status Code 302
  - [x] 로그인 성공시 /index.html 리다이렉트
- [x] POST 방식으로 회원가입
  - [x] register html 응답
  - [x] 회원가입 처리
  - [x] 로그인 get -> post 변경
- [x] CSS 지원하기

## 2 단계
- [x] RequestMapping: uri로 컨트롤러 반환

## 3 단계
- [x] HttpCookie 구현
  - [x] Cookie 파싱
- [ ] HttpSession 구현
  - [x] 쿠키에 세션이 없다면 헤더에 `Set-Cookie`로 새로운 세션ID를 넣어준다
  - [x] 로그인 성공시 세션에 유저를 저장한다.
  - [x] 로그인 시 세션ID를 통해 세션에 유저정보가 있는지 먼저 검사한다
  - [ ] 세션 만료 구현

## 추후 리팩토링
- [ ] 상수 추출
- [ ] RuntimeException 제거 or 커스텀 예외로 변경
- [ ] Header enum으로 관리
- [ ] HttpResponse.errorPage() -> ExceptionHandler를 통해 처리