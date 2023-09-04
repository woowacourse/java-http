# 톰캣 구현하기

## 1단계 - HTTP 서버 구현하기
- [x] 주어진 코드 수정하기
  - [x] 주어진 요청이 있는지 판단하는 HandlerMapping 구현
  - [x] 요청에 따른 응답을 생성하는 Handler 구현
- [x] GET /index.html 응답하기
  - [x] string을 반환하는 요청과 file을 반환하는 요청을 분리
- [x] CSS 및 JS 지원하기
  - [x] CSS 및 JS 요청에 대한 HandlerMapping 요소 추가
- [x] Query String 파싱하기
  - [x] 로그인 요청에 대한 HandlerMapping 요소 추가
  - [x] QueryString 객체 생성
  - [x] login에서 쿼리 요청시 존재하는 사용자이면 로그 출력
## 2단계 - 로그인 구현하기
- [x] 로그인 요청
  - [x] 있는 회원이면 index.html로 리다이렉트 하기
  - [x] 없는 회원이면 401.html로 리다이렉트 하기
- [ ] POST 방식으로 회원가입
- [ ] Cookie에 JSESSIONID값 저장하기
- [ ] Session 구현하기
