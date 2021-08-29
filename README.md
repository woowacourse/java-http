# HTTP 서버 구현하기

### 🛠 구현할 기능 목록
- [x] 학습 테스트 작성 및 공부

- [x] GET /index.html 응답하기
    - [x] "http://localhost:8080/index.html"에 접속 시 index.html 페이지 보여줌

- [x] GET /login 응답하기
    - [x] "http://localhost:8080/login"에 접속 시 login.html 페이지 보여줌
    - [x] "http://localhost:8080/login?account=gugu&password=password"에 접속 시 회원 조회
        - [x] 쿼리스트링에서 account, password 추출하기
        - [x] InMemoryUserRepository를 통해 가입되어 있는 회원인지 확인
            - [x] 로그도 함께 남길 것

- [x] 로그인 성공/실패 리다이렉트
    - [x] 로그인 성공 시 /index.html로 리다이렉트
        - [x] 응답 헤더에 http status code 302로 반환
    - [x] 로그인 실패 시 401.html로 리다이렉트
        - [x] GET /401.html 만들기

- [x] POST /register 회원 가입
    - [x] "http://localhost:8080/register" 접속 시 register.html 보여줌
    - [x] 회원 가입 버튼 누르면 POST를 사용하여 요청
    - [x] 회원 가입 완료시 index.html로 리다이렉트
    - [x] 로그인 버튼 누르면 POST 방식을 사용하도록 변경

- [x] GET /css/cover.css 응답하기

### 📜 구현의 흐름
- [ ] ServerSocket에 클라이언트 요청이 오면 새로운 Socket 생성 후 연결
    - [ ] Thread Pool 제한?

- [x] 클라이언트의 요청이 정적 파일을 요청하는 것인지 검사
    - [x] 정적 파일들의 경우 미리 생성해두면 좋을 듯 함
    - [x] Map\<ClientRequest, File>로 캐싱해서 가지고 있으면 좋을 듯
    - [x] 알맞은 ClientRequest 라면 File을 바로 반환할 것

- [x] 클라이언트의 요청이 동적 처리를 요청하는지 검사
    - [x] 서버 처음 시작하기 전에 Bean으로 등록한 객체들을 싱글톤으로 띄워둘 것
        - [x] 해당 Bean에서 `@GetMapping(path)`, `@PostMappig(path)` 등을 검사하여 Map\<ClientRequest, Map<Object, Method>> 로 저장해두기

- [x] 정적/동적 모두 매핑될 정보가 없으면 404.html 반환할 것
