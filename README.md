# 톰캣 구현하기

## 기능 구현 목록

- [x] 요청을 분리하는 request 구현
    - [x] uri 분리하는 기능 구현
    - [x] params 분리하는 기능 구현
    - [x] 요청 Header를 분리하는 기능 구현
- [x] 응답을 분리하는 response 구현
    - [x] HTTP Version과 상태코드를 반환해주는 기능 구현
    - [x] Header를 내려주는 기능 구현
    - [x] 상황에 맞는 응답을 줄 수 있도록 응답 메서드 구현

- [x] GET index.html 응답
    - [x] html외 자원 반환하기 (response에서 해결)

- [x] Query String 파싱하기
    - uri에서 params 추출하기
    - 로그인에 성공하면 로깅

- [x] Register
  - [x] GET, POST 요청 나눠서 처리하는 기능 추가
  - [x] 회원가입 기능 구현

- [x] Login
  - [x] 로그인 GET, POST 요청을 나누기
  - [x] 세션, 쿠키 적용
  - [x] 로그인 한 유저가 로그인 페이지를 가면 index.html로 리다이렉트

- [x] 리팩토링
- [x] 동시성 확장
