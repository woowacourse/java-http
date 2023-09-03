# 톰캣 구현하기

## 기능 요구사항

### 1단계 HTTP 서버 구현하기

- [x] 정적 자원에 대한 응답 구현
    - [x] GET /index.html 응답하기
    - [x] CSS 지원하기
- [x] REST API 요청 핸들러 구현
    - [x] Query String 파싱
    - [x] 회원 조회
    - [x] 응답객체 생성

### 2단계 로그인 구현하기

- [x] 로그인 여부에 따라 다른 페이지로 리다이렉트하기
    - [x] 로그인 응답정보 수정하기
- [x] POST 방식으로 회원가입
    - [ ] 로그인도 POST 방식으로 수정
- [ ] Cookie에 JSESSIONID 값 저장하기
- [ ] Session 구현하기
