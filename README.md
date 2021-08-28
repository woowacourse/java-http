# HTTP 서버 구현하기

### 🛠 구현할 기능 목록
- [x] 학습 테스트 작성 및 공부

- [x] GET /index.html 응답하기
    - [x] "http://localhost:8080/index.html"에 접속 시 index.html 페이지 보여줌

- [ ] GET /login 응답하기
    - [ ] "http://localhost:8080/login"에 접속 시 login.html 페이지 보여줌
    - [ ] "http://localhost:8080/login?account=gugu&password=password"에 접속 시 회원 조회
        - [ ] 쿼리스트링에서 account, password 추출하기
        - [ ] InMemoryUserRepository를 통해 가입되어 있는 회원인지 확인
            - [ ] 로그도 함께 남길 것

- [ ] 로그인 성공/실패 리다이렉트
    - [ ] 로그인 성공 시 /index.html로 리다이렉트
        - [ ] 응답 헤더에 http status code 302로 반환
    - [ ] 로그인 실패 시 401.html로 리다이렉트
        - [ ] GET /401.html 만들기

- [ ] POST /register 회원 가입
    - [ ] "http://localhost:8080/register" 접속 시 register.html 보여줌
    - [ ] 회원 가입 버튼 누르면 POST를 사용하여 요청
    - [ ] 회원 가입 완료시 index.html로 리다이렉트
    - [ ] 로그인 버튼 누르면 POST 방식을 사용하도록 변경

- [ ] GET /css/cover.css 응답하기
