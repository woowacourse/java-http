# 1단계 - HTTP 서버 구현하기

## 기능 요구 사항
- [x] `GET /index.html` 응답하기
  - 인덱스 페이지에 접근할 수 있도록 만들기
  - `Http11ProcessorTest` 테스트 모두 통과하기
- [x] CSS 지원하기
    - 사용자 페이지를 열었을 때 CSS 파일도 호출하도록 기능 추가하기
- [x] Query String 파싱
  - `http://localhost:8080/login?account=usher&password=password` 접속하면 로그인 페이지(login.html) 보여주기
  - Query String 파싱해서 아이디, 비밀번호 일치하면 콘솔창에 로그로 회원을 조회한 결과 나오도록 만들기
