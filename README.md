# HTTP 서버 구현하기

## 3단계 - 쿠키, 세션 구현하기

### 요구사항
- [ ] 쿠키 구현하기
    - [ ] 요청 헤더 쿠키에 JSESSIONID가 없으면 응답 헤더에 Set-Cookie를 추가하고 JSESSIONID=value 형태로 값을 전달한다.
    - [ ] JSESSIONID 값으로 UUID 클래스를 사용한다.
    
- [ ] 세션 구현하기
    - [ ] 로그인에 성공하면 HttpSession 객체의 값으로 User 객체를 저장한다.
    - [ ] 로그인된 상태에서 GET /login 으로 접근하면 index.html로 리다이렉트 처리한다.
