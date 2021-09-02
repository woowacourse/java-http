# HTTP 서버 구현하기

## 3단계 - 쿠키, 세션 구현하기

### 요구사항
- [x] 쿠키 구현하기
    - [x] 로그인 성공하면 응답 헤더에 Set-Cookie에 SESSIONID=value 형태로 추가한다.
    - [x] JSESSIONID 값으로 UUID 클래스를 사용한다.
    
- [x] 세션 구현하기
    - [x] 로그인에 성공하면 HttpSession 객체의 값으로 User 객체를 저장한다.
    - [x] 로그인된 상태에서 GET /login 으로 접근하면 index.html로 리다이렉트 처리한다.
