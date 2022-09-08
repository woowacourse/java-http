# 톰캣 구현하기

### 기능 요구사항
- [x] 리소스 응답하기
    - [x] html, js, css 파일에 대한 응답
- [x] Query String 파싱하기


### 2/3단계 구현 목록
- [x] HTTP Request 클래스에 header 를 받도록 수정
- [x] HTTP Response 클래스 만들어 리팩터링
- [x] Controller 인터페이스와 HandlerMapping 클래스 활용 if문 제거

- [x] 로그인 성공시 302 응답과 함께 /index.html로 리다이렉트
- [x] 로그인 실패시 401.html로 리다이렉트
- [x] 로그인 요청을 POST로 전환
- [x] 회원가입 완료시 index.html로 리다이렉트
- [x] request의 쿠키에 JSESSIONID가 없으면 새로운 JSESSIONID를 생성해 쿠키에 담아 응답
- [x] 로그인 요청 성공시 요청의 쿠키에 담긴 Session으로 user 객체 저장
- [x] 이미 로그인한 사용자의 /login 페이지 접근을 /index.html 로 리다이렉트
